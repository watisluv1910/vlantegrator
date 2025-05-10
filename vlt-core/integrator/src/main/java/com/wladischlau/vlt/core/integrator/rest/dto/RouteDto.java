package com.wladischlau.vlt.core.integrator.rest.dto;

import com.wladischlau.vlt.core.commons.dto.RouteIdDto;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;
import java.util.Map;

@Schema(description = "Конфигурация маршрута")
public record RouteDto(
        @Schema(description = "Идентификатор маршрута (uuid + versionHash)")
        RouteIdDto routeId,
        @Schema(description = "Имя маршрута")
        String name,
        @Schema(description = "Описание маршрута")
        String description,
        @Schema(description = "Username владельца. Если пустой – владельцем маршрута становится инициатор запроса")
        String ownerName,
        @Schema(description = "Маппинг портов")
        String publishedPorts,
        @Schema(description = "Сети маршрута. Должны быть предварительно созданы")
        List<String> networks,
        @Schema(description = "Конфигурация среды маршрута")
        Map<String, Object> env) {
}
