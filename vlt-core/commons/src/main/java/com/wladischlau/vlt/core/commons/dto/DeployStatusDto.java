package com.wladischlau.vlt.core.commons.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.ZonedDateTimeSerializer;
import com.wladischlau.vlt.core.commons.model.deploy.DeployActionType;
import com.wladischlau.vlt.core.commons.model.kafka.KafkaStatusCode;
import lombok.Builder;

import java.time.ZonedDateTime;

@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public record DeployStatusDto
        (@JsonProperty("routeId") RouteIdDto routeId,
         @JsonProperty("status") KafkaStatusCode status,
         @JsonProperty("action") DeployActionType action,
         @JsonProperty("message") String message,
         @JsonProperty("timestamp")
         @JsonSerialize(using = ZonedDateTimeSerializer.class)
         ZonedDateTime timestamp) {
}
