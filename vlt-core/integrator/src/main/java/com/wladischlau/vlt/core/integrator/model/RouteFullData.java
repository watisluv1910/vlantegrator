package com.wladischlau.vlt.core.integrator.model;

import java.util.List;

public record RouteFullData(
        Route info,
        List<NodeFullData> nodes,
        List<ConnectionFullData> connections
) {
}
