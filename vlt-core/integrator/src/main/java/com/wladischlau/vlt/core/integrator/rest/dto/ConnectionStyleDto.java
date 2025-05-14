package com.wladischlau.vlt.core.integrator.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Стилизация соединения")
public record ConnectionStyleDto(
        @Schema(description = "Тип соединения", requiredMode = Schema.RequiredMode.REQUIRED)
        String type,
        @Schema(description = "Тип начала соединения", requiredMode = Schema.RequiredMode.REQUIRED)
        String startMarkerType,
        @Schema(description = "Тип конца соединения", requiredMode = Schema.RequiredMode.REQUIRED)
        String endMarkerType,
        @Schema(description = "Соединение анимировано", requiredMode = Schema.RequiredMode.REQUIRED)
        boolean animated,
        @Schema(description = "Соединение можно взять в фокус", requiredMode = Schema.RequiredMode.REQUIRED)
        boolean focusable
) {
}
