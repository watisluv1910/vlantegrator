package com.wladischlau.vlt.core.integrator.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Координаты узла на canvas")
public record NodePositionDto(
        @Schema(description = "Координата X")
        int x,
        @Schema(description = "Координата Y")
        int y,
        @Schema(description = "Z-индекс узла")
        int zIndex
) {
}
