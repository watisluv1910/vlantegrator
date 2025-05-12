package com.wladischlau.vlt.core.integrator.model;

import com.wladischlau.vlt.core.commons.model.RouteId;

import java.time.Instant;

public record RouteUserAction(
        RouteId routeId,
        String username,
        String userDisplayName,
        RouteAction action,
        Instant attemptedAt
) {
}
