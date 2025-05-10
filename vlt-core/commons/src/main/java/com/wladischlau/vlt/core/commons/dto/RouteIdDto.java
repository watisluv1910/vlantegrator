package com.wladischlau.vlt.core.commons.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "Полный идентификатор маршрута")
public record RouteIdDto(
        @Schema(description = "ID маршрута")
        @JsonProperty("id") UUID id,
        @Schema(description = "Хэш-код версии маршрута")
        @JsonProperty("versionHash") String versionHash) {

    public String full() {
        return id + "." + versionHash;
    }
}
