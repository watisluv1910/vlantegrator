package com.wladischlau.vlt.core.integrator.rest.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public record BuildRouteRequestDto(@JsonProperty("id") UUID routeId, @JsonProperty("versionHash") String versionHash) {
}
