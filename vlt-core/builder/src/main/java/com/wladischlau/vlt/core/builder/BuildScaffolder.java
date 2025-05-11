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
import java.io.UncheckedIOException;
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
            String msg = MessageFormat.format("Route can not be prepared for build procedure [path: {0}]",
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
            log.debug("POM file already exists in route dir, removing... [path: {}]", mavenPomPath);
            try {
                Files.delete(mavenPomPath);
            } catch (IOException e) {
                var msg = MessageFormat.format("Unable to delete existing file [path: {0}]", routePathInfo);
                log.error(msg, e);
                throw new UncheckedIOException(msg, e);
            }
        }

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
        log.info("Generated POM-file for route {}.{}", routePathInfo.routeUuid(), routePathInfo.commitHash());
    }

    private void generateSpringConfiguration(RoutePathInfo routePathInfo) {
        var springConfigPath = getRouteResourcesDir(routePathInfo.commitDir()).resolve("application.properties");
        if (Files.exists(springConfigPath)) {
            log.debug("Spring application.properties file already exists in route dir, removing... [path: {}]", springConfigPath);
            try {
                Files.delete(springConfigPath);
            } catch (IOException e) {
                var msg = MessageFormat.format("Unable to delete existing file [path: {0}]", routePathInfo);
                log.error(msg, e);
                throw new UncheckedIOException(msg, e);
            }
        }

        var model = new HashMap<String, Object>();
        var routeConfig = new HashMap<String, Object>();
        routeConfig.put("name", routePathInfo.routeUuid() + "." + routePathInfo.commitHash());
        model.put("route", routeConfig);

        freemarker.generateFile("application.properties.ftl", model, springConfigPath);
        log.info("Generated application.properties file for route {}.{}", routePathInfo.routeUuid(),
                 routePathInfo.commitHash());
    }

    @SneakyThrows
    private void copyAdditionalResources(RoutePathInfo routePathInfo) {
        var resolver = new PathMatchingResourcePatternResolver();
        var resources = resolver.getResources(ROUTE_CONFIG_CLASSPATH);

        if (resources.length == 0) {
            log.debug("Additional route configuration resources were not found [path: {}]", ROUTE_CONFIG_CLASSPATH);
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
                log.debug("Configuration resource file already exists in route dir, removing... [path: {}]", targetFile);
                try {
                    Files.delete(targetFile);
                } catch (IOException e) {
                    var msg = MessageFormat.format("Unable to delete existing file [path: {0}]", targetFile);
                    log.error(msg, e);
                    throw new UncheckedIOException(msg, e);
                }
            }

            Files.createDirectories(targetFile.getParent());
            try (var in = resource.getInputStream()) {
                Files.copy(in, targetFile, StandardCopyOption.REPLACE_EXISTING);
                copied.add(targetFile.getFileName());
            }

            log.debug("Resource {} copied in {}", resource.getFilename(), targetFile);
        }

        if (copied.isEmpty()) {
            log.info("All additional configuration resources are already present in route {}.{}",
                     routePathInfo.routeUuid(), routePathInfo.commitHash());
        } else {
            log.info("Additional configuration resources copied to route {}.{} [resources: {}]",
                     routePathInfo.routeUuid(), routePathInfo.commitHash(),
                     String.join(",", copied.stream().map(Path::toString).toList()));
        }
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

        log.info("Maven Wrapper copied to the directory: {}", dir);
    }
}