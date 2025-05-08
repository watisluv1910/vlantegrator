package com.wladischlau.vlt.core.integrator.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "Ответ на запрос на создание маршрута")
public record RouteIdDto(
        @Schema(description = "ID маршрута")
        UUID id,
        @Schema(description = "Хэш-код версии маршрута")
        String versionHash) {
}
