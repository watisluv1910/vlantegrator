package com.wladischlau.vlt.core.integrator.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

@Schema(description = "Пользовательские настройки accessibility")
public record UserAccessibilitySettingsDto(
        @Schema(description = "Выключить анимации", requiredMode = RequiredMode.REQUIRED)
        boolean disableAnimations,
        @Schema(description = "Включить режим высокой контрастности", requiredMode = RequiredMode.REQUIRED)
        boolean enableHighContrast
) {}
