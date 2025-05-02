package com.wladischlau.vlt.core.builder.util;

import com.wladischlau.vlt.core.builder.BuildScaffolder;
import com.wladischlau.vlt.core.builder.MavenCompiler;
import com.wladischlau.vlt.core.builder.model.RoutePathInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.UUID;

@Slf4j
@Service
public class RouteWatcherHelper {

    public static final String COMMIT_DIR_PREFIX = "commit-";

    @Async(value = "routeBuildTaskExecutor")
    public void handleRouteChange(Path file, BuildScaffolder scaffolder, MavenCompiler compiler) {
        var routeInfo = resolveRouteInfo(file);

        log.info("Обнаружено изменение в маршруте {}@{} [file: {}]", routeInfo.routeUuid(), routeInfo.commitHash(), file.getFileName());
        try {
            scaffolder.scaffold(routeInfo);
            compiler.compile(routeInfo.commitDir());
        } catch (Exception e) {
            log.error("Ошибка при сборке маршрута {}@{}", routeInfo.routeUuid(), routeInfo.commitHash(), e);
        }
    }

    private RoutePathInfo resolveRouteInfo(Path file) {
        Path curr = Path.of(file.toString());
        while (curr != null && curr.getFileName() != null &&
                !curr.getFileName().toString().startsWith(COMMIT_DIR_PREFIX)) {
            curr = curr.getParent();
        }

        if (curr == null || curr.getFileName() == null || curr.getParent() == null) {
            var msg = MessageFormat.format("Не удалось найти родительскую директорию commit-<hash> [file: {0}]",
                                           file.toString());
            log.error(msg);
            throw new IllegalStateException(msg);
        }

        var commitHash = curr.getFileName().toString().substring(COMMIT_DIR_PREFIX.length());

        var routeDir = curr.getParent();
        var routeUuid = UUID.fromString(routeDir.getFileName().toString());
        return new RoutePathInfo(routeUuid, commitHash, routeDir, curr);
    }
}
