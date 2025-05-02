package com.wladischlau.vlt.core.intergator.service;

import com.wladischlau.vlt.core.intergator.IntegrationFlowGenerator;
import com.wladischlau.vlt.core.intergator.model.Route;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RouteBuildService {

    public static final String COMMIT_DIR_PREFIX = "commit-";

    private final IntegrationFlowGenerator integrationFlowGenerator;

    @Async
    public void buildRouteAsync(UUID routeId, String commitHash, Route route) {
        Path outputDir = Path.of(System.getProperty("user.home"),
                                 ".vlt", "cache", "routes", // TODO: Move to common
                                 routeId.toString(), COMMIT_DIR_PREFIX + commitHash,
                                 "src", "main", "java"); // TODO: Move to common
        if (!Files.exists(outputDir)) {
            try {
                Files.createDirectories(outputDir);
            } catch (IOException e) {
                log.error("Unable to create output directory: {}", outputDir, e);
                throw new UncheckedIOException(e);
            }
        }
        integrationFlowGenerator.generateFlowConfig(route, outputDir);
    }
}
