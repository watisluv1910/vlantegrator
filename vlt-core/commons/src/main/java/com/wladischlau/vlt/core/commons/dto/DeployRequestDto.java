package com.wladischlau.vlt.core.commons.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.wladischlau.vlt.core.commons.model.deploy.DeployActionType;
import lombok.Builder;

import java.util.List;
import java.util.Map;

@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public record DeployRequestDto(
        @JsonProperty("routeId") RouteIdDto routeId,
        @JsonProperty("action") DeployActionType action,
        @JsonProperty("env") Map<String, Object> env,
        @JsonProperty("ports") String ports,
        @JsonProperty("networks") List<String> networks) {
}