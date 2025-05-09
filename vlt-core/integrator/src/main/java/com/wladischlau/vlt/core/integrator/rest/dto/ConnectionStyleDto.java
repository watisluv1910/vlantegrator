package com.wladischlau.vlt.core.integrator.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Стилизация соединения")
public record ConnectionStyleDto(
        @Schema(description = "Тип соединения")
        String type,
        @Schema(description = "Тип начала соединения")
        String startMarkerType,
        @Schema(description = "Тип конца соединения")
        String endMarkerType,
        @Schema(description = "Соединение анимировано")
        boolean animated,
        @Schema(description = "Соединение можно взять в фокус")
        boolean focusable
) {
}
