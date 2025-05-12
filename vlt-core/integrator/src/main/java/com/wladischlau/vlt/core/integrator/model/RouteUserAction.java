package com.wladischlau.vlt.core.integrator.model;

import com.wladischlau.vlt.core.commons.model.RouteId;
import lombok.Builder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.time.Instant;
import java.util.UUID;

@Builder
public record RouteUserAction(
        RouteId routeId,
        String username,
        String userDisplayName,
        RouteAction action,
        Instant attemptedAt
) {
    public RouteUserAction(RouteId routeId, String username, String userDisplayName, RouteAction action) {
        this(routeId, username, userDisplayName, action, Instant.now());
    }

    public RouteUserAction(RouteId routeId, JwtAuthenticationToken principal, RouteAction action) {
        this(routeId, principal.getName(), principal.getToken().getClaimAsString("name"), action);
    }

    public RouteUserAction(UUID routeId, String versionHash, JwtAuthenticationToken principal, RouteAction action) {
        this(new RouteId(routeId, versionHash), principal, action);
    }

    public RouteUserAction(UUID routeId, JwtAuthenticationToken principal, RouteAction action) {
        this(routeId, null, principal, action);
    }
}
