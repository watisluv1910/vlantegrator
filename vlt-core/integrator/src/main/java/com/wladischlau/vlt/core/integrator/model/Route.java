package com.wladischlau.vlt.core.integrator.model;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Map;

@Validated
@Builder
public record Route(
        RouteId routeId,
        String name,
        String description,
        String owner,
        List<Pair<@NotNull Integer, @NotNull Integer>> publishedPorts,
        Map<String, Object> env,
        List<RouteNetwork> networks
) {
}
