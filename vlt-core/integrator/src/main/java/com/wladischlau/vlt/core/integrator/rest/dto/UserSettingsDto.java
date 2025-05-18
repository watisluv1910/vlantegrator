package com.wladischlau.vlt.core.integrator.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

@Schema(description = "Пользовательские настройки. Не влияют на работу всей платформы")
public record UserSettingsDto(
        @Schema(description = "Настройки редактора маршрутов", requiredMode = RequiredMode.REQUIRED)
        UserEditorSettingsDto editor,
        @Schema(description = "Настройки редактора accessibility", requiredMode = RequiredMode.REQUIRED)
        UserAccessibilitySettingsDto accessibility
) {}
