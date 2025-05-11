package com.wladischlau.vlt.core.builder;

import com.wladischlau.vlt.core.builder.util.FileUtil;
import com.wladischlau.vlt.core.builder.util.RouteWatcherHelper;
import jakarta.annotation.Nonnull;
import jakarta.annotation.PreDestroy;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.io.IOException;
import java.nio.file.ClosedWatchServiceException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Set;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;
import static java.nio.file.StandardWatchEventKinds.OVERFLOW;

@Slf4j
@Service
@Validated
public class RouteWatcher implements Runnable {

    private static final Set<String> WATCHER_IGNORED_FILES = Set.of(".DS_Store", ".gitignore", ".gitattributes",
                                                                    "build.log", "RouteApplication.java", "logback.xml");
    private static final Set<String> WATCHER_IGNORED_DIRS = Set.of("target", "build", ".git", ".idea");

    @Getter
    private final Path routesCache;
    private final BuildScaffolder scaffolder;
    private final MavenCompiler compiler;
    private final FileUtil fileUtil;

    private final WatchService watchService;
    private final Thread watchThread;
    private final RouteWatcherHelper helper;

    public RouteWatcher(BuildScaffolder scaffolder, FileUtil fileUtil, RouteWatcherHelper helper) throws IOException {
        this.scaffolder = scaffolder;
        this.helper = helper;
        this.fileUtil = fileUtil;

        // ~/.vlt/cache/routes
        this.routesCache = Path.of(System.getProperty("user.home"), ".vlt", "cache", "routes"); // TODO: Move in common
        if (!Files.exists(routesCache)) {
            Files.createDirectories(routesCache);
        }

        if (!Files.exists(routesCache.resolve(".mvn"))) {
            scaffolder.copyMavenWrapperIfNeeded(routesCache);
        }

        String mvnwName = System.getProperty("os.name").toLowerCase().contains("win") ? "mvnw.cmd" : "mvnw";
        this.compiler = new MavenCompiler(routesCache.resolve(mvnwName));

        this.watchService = FileSystems.getDefault().newWatchService();
        registerAllSubdirectories(routesCache);

        this.watchThread = new Thread(this, "RouteDirectoryWatcher");
        this.watchThread.start();
        log.info("RouteDirectoryWatcher запущен и отслеживает [path: {}]", routesCache);
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            WatchKey key;
            try { // Блокируется до появления события
                key = watchService.take();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.warn("Наблюдение прервано [path: {}]", routesCache, e);
                break;
            } catch (ClosedWatchServiceException e) {
                log.error("WatchService закрыт, наблюдение невозможно [path: {}]", routesCache, e);
                break;
            }

            for (var event : key.pollEvents()) {
                var kind = event.kind();

                if (kind == OVERFLOW || kind == ENTRY_DELETE)
                    continue;

                @SuppressWarnings("unchecked")
                Path context = ((WatchEvent<Path>) event).context();
                // key.watchable() – директория, к которой прикреплён watch
                Path dir = (Path) key.watchable();
                Path child = dir.resolve(context);

                String fileName = child.getFileName().toString();
                if (WATCHER_IGNORED_FILES.contains(fileName)) {
                    log.debug("Файл {} игнорируется WatchService", child);
                    continue;
                }

                try {
                    if (kind == ENTRY_CREATE) {
                        if (Files.isDirectory(child)) {
                            if (WATCHER_IGNORED_DIRS.contains(child.getFileName().toString())) {
                                log.debug("Пропуск регистрации игнорируемой директории: {}", child);
                                continue;
                            }

                            registerAllSubdirectories(child);

                            try (var files = Files.walk(child)) {
                                files.filter(fileUtil::isJavaFile)
                                        .forEach(file -> helper.handleRouteChange(file, scaffolder, compiler));
                            }
                        } else if (fileUtil.isJavaFile(child)) {
                            helper.handleRouteChange(child, scaffolder, compiler);
                        }
                    } else if (kind == ENTRY_MODIFY && fileUtil.isJavaFile(child)) {
                        helper.handleRouteChange(child, scaffolder, compiler);
                    }
                } catch (Exception e) {
                    log.error("Ошибка при обработке события [file: {}]", child, e);
                }
            }

            boolean valid = key.reset();
            if (!valid) {
                Path watchedDir = (Path) key.watchable();
                log.debug("WatchKey для директории {} перестал быть валидным", watchedDir);

                if (watchedDir.equals(routesCache)) {
                    log.warn("Событие {} перестало быть валидным, завершение наблюдения за {}", key, routesCache);
                    break;
                }
            }
        }

        try {
            watchService.close();
        } catch (IOException e) {
            log.error("Ошибка при закрытии WatchService [path: {}]", routesCache, e);
        }
    }

    private void registerAllSubdirectories(Path start) throws IOException {
        Files.walkFileTree(start, new SimpleFileVisitor<>() {
            @Override
            public @Nonnull FileVisitResult preVisitDirectory(Path dir,
                                                              @Nonnull BasicFileAttributes attrs) throws IOException {
                String dirName = dir.getFileName().toString();
                if (WATCHER_IGNORED_DIRS.contains(dirName)) {
                    log.debug("Пропущена директория: {}", dir);
                    return FileVisitResult.SKIP_SUBTREE;
                }

                dir.register(watchService, ENTRY_CREATE, ENTRY_MODIFY);
                log.debug("WatchService зарегистрирован для директории [path: {}]", dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    @PreDestroy
    public void stop() throws IOException {
        watchThread.interrupt();
        watchService.close();
    }
}