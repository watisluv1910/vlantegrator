package com.wladischlau.vlt.core.integrator.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

import java.util.List;

@Schema(description = "Структура маршрута")
public record RouteDefinitionDto(
        @Schema(description = "Узлы маршрута",
                requiredMode = RequiredMode.REQUIRED)
        List<NodeDto> nodes,
        @Schema(description = "Соединения маршрута",
                requiredMode = RequiredMode.REQUIRED)
        List<ConnectionDto> connections
) {
}
