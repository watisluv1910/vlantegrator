package com.wladischlau.vlt.core.integrator.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "Соединение узлов")
public record ConnectionDto(
        @Schema(description = "Идентификатор узла-источника")
        UUID sourceId,
        @Schema(description = "Идентификатор узла-цели")
        UUID targetId,
        @Schema(description = "Стили соединения")
        ConnectionStyleDto style) {
}
