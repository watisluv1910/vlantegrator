package com.wladischlau.vlt.core.integrator.model;

import java.util.List;

public record RouteCacheData(
        List<NodeFullData> nodes,
        List<ConnectionFullData> connections
) {
}
