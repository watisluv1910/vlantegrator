package com.wladischlau.vlt.core.integrator.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

import java.util.Map;

@Schema(description = "Стилизация узла")
public record NodeStyleDto(
        @Schema(description = "Тип узла",
                requiredMode = RequiredMode.REQUIRED,
                examples = {"default", "input", "output", "group"})
        String type,
        @Schema(description = "Стили узла", requiredMode = RequiredMode.REQUIRED)
        Map<String, Object> config
) {
}
