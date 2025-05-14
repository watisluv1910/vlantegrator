package com.wladischlau.vlt.core.integrator.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

@Schema(description = "Координаты узла на canvas")
public record NodePositionDto(
        @Schema(description = "Координата X", requiredMode = RequiredMode.REQUIRED)
        int x,
        @Schema(description = "Координата Y", requiredMode = RequiredMode.REQUIRED)
        int y,
        @Schema(description = "Z-индекс узла", requiredMode = RequiredMode.REQUIRED)
        int zIndex
) {
}
