package com.wladischlau.vlt.core.builder.model;

import java.nio.file.Path;
import java.util.UUID;

public record RoutePathInfo(UUID routeUuid, String commitHash, Path routeDir, Path commitDir) {
}
