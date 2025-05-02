package com.wladischlau.vlt.core.builder;

import com.wladischlau.vlt.core.builder.config.DockerRegistryProperties;
import com.wladischlau.vlt.core.builder.config.MavenRepoProperties;
import com.wladischlau.vlt.core.builder.model.RoutePathInfo;
import com.wladischlau.vlt.core.builder.util.FileUtil;
import com.wladischlau.vlt.core.builder.util.FreemarkerUtil;
import com.wladischlau.vlt.core.builder.util.MavenPomPropertiesExtractor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.PosixFilePermissions;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

@Slf4j
@Service
public class BuildScaffolder {

    public static final String ROUTE_BUILDPACK_PREFIX = "ROUTE_BUILDPACK_";

    public static final String ROUTE_CONFIG_PATH = "route/config";
    public static final String ROUTE_CONFIG_CLASSPATH = "classpath:/" + ROUTE_CONFIG_PATH + "/**";

    private final DockerRegistryProperties dockerRegistryProperties;
    private final MavenRepoProperties mavenRepoProperties;
    private final ConfigurableEnvironment environment;
    private final FreemarkerUtil freemarker;
    private final FileUtil fileUtil;

    private final MavenPomPropertiesExtractor mavenPomPropertiesExtractor;

    @Value("${route.builder}")
    private String routeBuilder;

    public BuildScaffolder(DockerRegistryProperties dockerRegistryProperties, MavenRepoProperties mavenRepoProperties,
                           ConfigurableEnvironment environment, FreemarkerUtil freemarker, FileUtil fileUtil) {
        this.dockerRegistryProperties = dockerRegistryProperties;
        this.mavenRepoProperties = mavenRepoProperties;
        this.environment = environment;
        this.freemarker = freemarker;
        this.fileUtil = fileUtil;

        var workingDir = Paths.get("").toAbsolutePath();
        var mavenConfigPath = workingDir.resolve("pom.xml");
        this.mavenPomPropertiesExtractor = new MavenPomPropertiesExtractor(mavenConfigPath);
    }

    /**
     * Метод scaffold создает (или обновляет) необходимые файлы в каталоге routeDir.
     *
     * @param routePathInfo данные о расположении маршрута.
     */
    public void scaffold(RoutePathInfo routePathInfo) {
        if (!Files.exists(routePathInfo.commitDir())) {
            String msg = MessageFormat.format("Маршрут не может быть подготовлен для сборки [path: {0}]",
                                              routePathInfo.commitDir().toAbsolutePath());
            log.error(msg);
            throw new IllegalStateException(msg);
        }

        generateMavenConfiguration(routePathInfo);
        generateSpringConfiguration(routePathInfo);

        copyAdditionalResources(routePathInfo);
    }

    private void generateMavenConfiguration(RoutePathInfo routePathInfo) {
        var mavenPomPath = routePathInfo.commitDir().resolve("pom.xml");
        if (Files.exists(mavenPomPath)) {
            log.debug("POM файл уже существует в файлах маршрута [path: {}]", mavenPomPath);
            return;
        }

        log.debug("POM файл отсутствует в файлах маршрута [path: {}]", routePathInfo.commitDir());

        var model = new HashMap<String, Object>();
        var mavenProps = mavenPomPropertiesExtractor.getProperties();

        var versions = new HashMap<String, Object>();
        versions.put("vlt", mavenProps.get("vlt.version"));
        versions.put("spring_boot", mavenProps.get("spring-boot.version"));
        versions.put("spring_dotenv", mavenProps.get("spring-dotenv.version"));
        versions.put("maven_compiler_plugin", mavenProps.get("maven-compiler-plugin.version"));
        versions.put("java", mavenProps.get("java.version"));
        versions.put("lombok", mavenProps.get("lombok.version"));
        model.put("versions", versions);

        var dockerConfig = new HashMap<String, Object>();
        dockerConfig.put("image_registry", dockerRegistryProperties.imageRegistry());
        dockerConfig.put("username", dockerRegistryProperties.username());
        dockerConfig.put("password", dockerRegistryProperties.password());
        model.put("docker", dockerConfig);

        var mavenRepoConfig = new HashMap<String, Object>();
        mavenRepoConfig.put("id", mavenRepoProperties.id());
        mavenRepoConfig.put("name", mavenRepoProperties.name());
        mavenRepoConfig.put("url", mavenRepoProperties.url());
        model.put("repo", mavenRepoConfig);

        var routeConfig = new HashMap<String, Object>();
        routeConfig.put("uuid", routePathInfo.routeUuid());
        routeConfig.put("commit_hash", routePathInfo.commitHash());

        var routeImageConfig = new HashMap<String, Object>();
        routeImageConfig.put("builder", routeBuilder);

        var routeBuildpacks = new HashMap<String, String>();
        environment.getSystemProperties().keySet().stream()
                .filter(it -> it.startsWith(ROUTE_BUILDPACK_PREFIX))
                .forEach(k -> routeBuildpacks.put(k, environment.getProperty(k)));

        routeImageConfig.put("buildpacks", routeBuildpacks);
        routeConfig.put("image", routeImageConfig);
        model.put("route", routeConfig);

        freemarker.generateFile("pom.ftl", model, mavenPomPath);
        log.info("Сгенерирован POM-файл для маршрута {}@{}", routePathInfo.routeUuid(), routePathInfo.commitHash());
    }

    private void generateSpringConfiguration(RoutePathInfo routePathInfo) {
        var springConfigPath = getRouteResourcesDir(routePathInfo.commitDir()).resolve("application.properties");
        if (Files.exists(springConfigPath)) {
            log.debug("Spring application.properties уже существует в файлах маршрута [path: {}]", springConfigPath);
            return;
        }

        log.debug("application.properties отсутствует в файлах маршрута [path: {}]", routePathInfo.commitDir());

        Map<String, Object> model = new HashMap<>();
        freemarker.generateFile("application.properties.ftl", model, springConfigPath);
        log.info("Сгенерирован application.properties-файл для маршрута {}.{}", routePathInfo.routeUuid(),
                 routePathInfo.commitHash());
    }

    @SneakyThrows
    private void copyAdditionalResources(RoutePathInfo routePathInfo) {
        var resolver = new PathMatchingResourcePatternResolver();
        var resources = resolver.getResources(ROUTE_CONFIG_CLASSPATH);

        if (resources.length == 0) {
            log.debug("Не найдено дополнительных ресурсов конфигурации маршрута [path: {}]", ROUTE_CONFIG_CLASSPATH);
            return;
        }

        log.debug("Копирование дополнительных ресурсов конфигурации маршрута из {} в {}",
                  ROUTE_CONFIG_CLASSPATH, routePathInfo.commitDir());

        var copied = new ArrayList<Path>(resources.length);
        for (var resource : resources) {
            if (!resource.isReadable() || resource.getFilename() == null)
                continue;

            var relativePath = fileUtil.computeRelativePath(resource, ROUTE_CONFIG_PATH);

            Path targetFile;
            if (resource.getFilename().endsWith(".java")) {
                targetFile = getRouteSourcesDir(routePathInfo.commitDir()).resolve(relativePath);
            } else {
                targetFile = routePathInfo.commitDir().resolve(relativePath);
            }

            if (Files.exists(targetFile)) {
                log.debug("Ресурс конфигурации уже имеется в директории маршрута [path: {}]", targetFile);
                continue;
            }

            Files.createDirectories(targetFile.getParent());
            try (var in = resource.getInputStream()) {
                Files.copy(in, targetFile, StandardCopyOption.REPLACE_EXISTING);
                copied.add(targetFile.getFileName());
            }

            log.debug("Ресурс {} скопирован в {}", resource.getFilename(), targetFile);
        }

        log.info("Дополнительные ресурсы конфигурации перенесены в папку маршрута {}.{} [resources: {}]",
                 routePathInfo.routeUuid(), routePathInfo.commitHash(),
                 String.join(",", copied.stream().map(Path::toString).toList()));
    }

    private static Path getRouteSourcesDir(Path dir) {
        return dir.resolve("src").resolve("main").resolve("java")
                .resolve("com").resolve("wladischlau").resolve("vlt").resolve("route");
    }

    private static Path getRouteResourcesDir(Path dir) {
        return dir.resolve("src").resolve("main").resolve("resources");
    }

    public void copyMavenWrapperIfNeeded(Path dir) throws IOException {
        var builderDir = Paths.get("").resolve("builder").toAbsolutePath();

        // mvnw
        var mvnw = builderDir.resolve("mvnw");
        if (Files.exists(mvnw)) {
            Files.copy(mvnw, dir.resolve("mvnw"), REPLACE_EXISTING);
            Files.setPosixFilePermissions(dir.resolve("mvnw"), PosixFilePermissions.fromString("rwxr-xr-x"));
        }

        // mvnw.cmd
        var mvnwCmd = builderDir.resolve("mvnw.cmd");
        if (Files.exists(mvnwCmd)) {
            Files.copy(mvnwCmd, dir.resolve("mvnw.cmd"), REPLACE_EXISTING);
        }

        // .mvn/wrapper/maven-wrapper.properties
        var wrapperSource = builderDir.resolve(".mvn").resolve("wrapper").resolve("maven-wrapper.properties");
        var wrapperTarget = dir.resolve(".mvn").resolve("wrapper").resolve("maven-wrapper.properties");

        Files.createDirectories(wrapperTarget.getParent());
        if (Files.exists(wrapperSource)) {
            Files.copy(wrapperSource, wrapperTarget, REPLACE_EXISTING);
        }

        log.info("Maven Wrapper добавлен в директорию: {}", dir);
    }
}