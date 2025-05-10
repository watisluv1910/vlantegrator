package com.wladischlau.vlt.core.integrator.rest.dto;

import com.wladischlau.vlt.core.commons.dto.RouteIdDto;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "Структура маршрута")
public record RouteDefinitionDto(
        @Schema(description = "Идентификатор маршрута")
        RouteIdDto id,
        @Schema(description = "Узлы маршрута")
        List<NodeDto> nodes,
        @Schema(description = "Соединения маршрута")
        List<ConnectionDto> connections
) {
}
