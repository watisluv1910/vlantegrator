package com.wladischlau.vlt.core.integrator.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "Соединение узлов")
public record ConnectionDto(
        @Schema(description = "Идентификатор узла-источника", requiredMode = Schema.RequiredMode.REQUIRED)
        UUID sourceId,
        @Schema(description = "Идентификатор узла-цели", requiredMode = Schema.RequiredMode.REQUIRED)
        UUID targetId,
        @Schema(description = "Стили соединения", requiredMode = Schema.RequiredMode.REQUIRED)
        ConnectionStyleDto style) {
}
