package com.wladischlau.vlt.core.integrator.rest.dto;

import com.wladischlau.vlt.core.commons.dto.RouteIdDto;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

import java.util.List;
import java.util.Map;

@Schema(description = "Конфигурация маршрута")
public record RouteDto(
        @Schema(description = "Идентификатор маршрута (uuid + versionHash)",
                requiredMode = RequiredMode.REQUIRED)
        RouteIdDto routeId,
        @Schema(description = "Имя маршрута",
                requiredMode = RequiredMode.REQUIRED)
        String name,
        @Schema(description = "Описание маршрута",
                requiredMode = RequiredMode.REQUIRED)
        String description,
        @Schema(description = "Username владельца. Если пустой – владельцем маршрута становится инициатор запроса",
                requiredMode = RequiredMode.REQUIRED)
        String ownerName,
        @Schema(description = "Маппинг портов",
                requiredMode = RequiredMode.REQUIRED)
        String publishedPorts,
        @Schema(description = "Сети маршрута. Должны быть предварительно созданы",
                requiredMode = RequiredMode.REQUIRED)
        List<String> networks,
        @Schema(description = "Конфигурация среды маршрута",
                requiredMode = RequiredMode.REQUIRED)
        Map<String, Object> env) {
}
