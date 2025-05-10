package com.wladischlau.vlt.core.integrator.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Структура маршрута")
public record RouteDefinitionDto(
        @Schema(description = "Узлы маршрута")
        List<NodeDto> nodes,
        @Schema(description = "Соединения маршрута")
        List<ConnectionDto> connections
) {
}
