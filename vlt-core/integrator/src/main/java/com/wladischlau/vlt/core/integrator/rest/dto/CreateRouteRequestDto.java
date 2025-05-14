package com.wladischlau.vlt.core.integrator.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.Map;

@Schema(description = "Запрос на создание маршрута")
public record CreateRouteRequestDto(
        @Schema(description = "Имя маршрута", requiredMode = Schema.RequiredMode.REQUIRED)
        String name,
        @Schema(description = "Описание маршрута", requiredMode = Schema.RequiredMode.REQUIRED)
        String description,
        @Schema(description = "Username владельца. Если пустой – владельцем маршрута становится инициатор запроса", requiredMode = Schema.RequiredMode.REQUIRED)
        String ownerName,
        @Schema(description = "Маппинг портов", requiredMode = Schema.RequiredMode.REQUIRED)
        String publishedPorts,
        @Schema(description = "Сети маршрута. Должны быть предварительно созданы", requiredMode = Schema.RequiredMode.REQUIRED)
        List<String> networks,
        @Schema(description = "Конфигурация среды маршрута", requiredMode = Schema.RequiredMode.REQUIRED)
        Map<String, Object> env
) {
}
