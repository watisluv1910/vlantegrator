package com.wladischlau.vlt.core.integrator.rest.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wladischlau.vlt.core.integrator.model.Connection;
import com.wladischlau.vlt.core.integrator.model.Node;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public record BuildRouteRequestDto(
        @JsonProperty("id") UUID routeId,
        @JsonProperty("commitHash") String commitHash,
        @JsonProperty("nodes") Set<Node> nodes,
        @JsonProperty("connections") List<Connection> connections) {
}
