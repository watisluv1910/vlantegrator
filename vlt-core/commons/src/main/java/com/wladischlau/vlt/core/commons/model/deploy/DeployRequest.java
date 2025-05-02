package com.wladischlau.vlt.core.commons.model.deploy;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wladischlau.vlt.core.commons.model.RouteId;
import lombok.Builder;

import java.util.List;
import java.util.Map;

@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public record DeployRequest(
        @JsonProperty("routeId") RouteId routeId,
        @JsonProperty("action") DeployActionType action,
        @JsonProperty("env") Map<String, Object> env,
        @JsonProperty("ports") String ports,
        @JsonProperty("networks") List<String> networks) {
}