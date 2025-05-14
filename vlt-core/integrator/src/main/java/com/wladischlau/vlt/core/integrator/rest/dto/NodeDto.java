package com.wladischlau.vlt.core.integrator.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

import java.util.Map;
import java.util.UUID;

@Schema(description = "Узел маршрута")
public record NodeDto(
        @Schema(description = "Идентификатор узла", requiredMode = RequiredMode.REQUIRED)
        UUID id,
        @Schema(description = "Идентификатор адаптера узла", requiredMode = RequiredMode.REQUIRED)
        UUID adapterId,
        @Schema(description = "Кастомное имя узла", requiredMode = RequiredMode.REQUIRED)
        String name,
        @Schema(description = "Конфиг узла", requiredMode = RequiredMode.REQUIRED)
        Map<String, Object> config,
        @Schema(description = "Стилизация узла", requiredMode = RequiredMode.REQUIRED)
        NodeStyleDto style,
        @Schema(description = "Координаты узла на canvas", requiredMode = RequiredMode.REQUIRED)
        NodePositionDto position
) {
}
