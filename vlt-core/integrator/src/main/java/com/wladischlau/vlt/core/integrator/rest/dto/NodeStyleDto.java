package com.wladischlau.vlt.core.integrator.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Map;

@Schema(description = "Стилизация узла")
public record NodeStyleDto(
        @Schema(description = "Тип узла", examples = {"default", "input", "output", "group"})
        String type,
        @Schema(description = "Стили узла")
        Map<String, Object> config
) {
}
