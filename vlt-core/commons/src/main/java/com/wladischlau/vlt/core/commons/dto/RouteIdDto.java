package com.wladischlau.vlt.core.commons.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "Полный идентификатор маршрута")
public record RouteIdDto(
        @Schema(description = "ID маршрута", requiredMode = Schema.RequiredMode.REQUIRED)
        @JsonProperty("id") UUID id,
        @Schema(description = "Хэш-код версии маршрута", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        @JsonProperty("versionHash") String versionHash) {

    public String full() {
        return id + "." + versionHash;
    }
}
