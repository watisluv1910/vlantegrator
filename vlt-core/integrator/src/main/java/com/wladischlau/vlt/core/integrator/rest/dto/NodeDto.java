package com.wladischlau.vlt.core.integrator.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Map;
import java.util.UUID;

@Schema(description = "Узел маршрута")
public record NodeDto(
        @Schema(description = "Идентификатор узла")
        UUID id,
        @Schema(description = "Идентификатор адаптера узла")
        UUID adapterId,
        @Schema(description = "Кастомное имя узла")
        String name,
        @Schema(description = "Конфиг узла")
        Map<String, Object> config,
        @Schema(description = "Стилизация узла")
        NodeStyleDto style,
        @Schema(description = "Координаты узла на canvas")
        NodePositionDto position
) {
}
