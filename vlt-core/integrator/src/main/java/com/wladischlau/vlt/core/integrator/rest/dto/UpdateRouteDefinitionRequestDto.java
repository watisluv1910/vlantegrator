package com.wladischlau.vlt.core.integrator.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.UUID;

@Schema(description = "Обновление структуры маршрута")
public record UpdateRouteDefinitionRequestDto(
        @Schema(description = "Идентификатор маршрута (uuid)")
        UUID id,
        @Schema(description = "Узлы маршрута")
        List<NodeDto> nodes,
        @Schema(description = "Соединения маршрута")
        List<ConnectionDto> connections
) {
}
