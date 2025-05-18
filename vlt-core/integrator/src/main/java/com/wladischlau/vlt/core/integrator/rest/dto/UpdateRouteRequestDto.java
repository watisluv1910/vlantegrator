package com.wladischlau.vlt.core.integrator.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

import java.util.List;
import java.util.Map;

@Schema(description = "Запрос на изменение конфигурации маршрута")
public record UpdateRouteRequestDto(
        @Schema(description = "Имя маршрута", requiredMode = RequiredMode.REQUIRED)
        String name,
        @Schema(description = "Описание маршрута", requiredMode = RequiredMode.NOT_REQUIRED)
        String description,
        @Schema(description = "Username владельца. Если пустой – владельцем маршрута становится инициатор запроса",
                requiredMode = RequiredMode.NOT_REQUIRED)
        String ownerName,
        @Schema(description = "Маппинг портов", requiredMode = RequiredMode.NOT_REQUIRED)
        String publishedPorts,
        @Schema(description = "Сети маршрута. Должны быть предварительно созданы", requiredMode = RequiredMode.REQUIRED)
        List<DockerNetworkDto> networks,
        @Schema(description = "Конфигурация среды маршрута", requiredMode = RequiredMode.REQUIRED)
        Map<String, Object> env
) {
}
