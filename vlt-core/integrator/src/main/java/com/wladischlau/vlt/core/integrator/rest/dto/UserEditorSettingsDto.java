package com.wladischlau.vlt.core.integrator.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

@Schema(description = "Пользовательские настройки редактора маршрутов")
public record UserEditorSettingsDto(
        @Schema(description = "Показывать ли сетку в редакторе маршрута", requiredMode = RequiredMode.REQUIRED)
        boolean showGrid,
        @Schema(description = "Позиция камеры по-умолчанию в редакторе маршрута", requiredMode = RequiredMode.REQUIRED)
        String defaultViewportPosition,
        @Schema(description = "Интервал в Мс, через который определение маршрута будет сохраняться автоматически. -1 для отключения",
                requiredMode = RequiredMode.REQUIRED)
        long autosaveIntervalMs
) {
}
