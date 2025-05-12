package com.wladischlau.vlt.core.integrator.rest.dto;

import com.wladischlau.vlt.core.commons.dto.RouteIdDto;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

@Schema(description = "Описание действия пользователя")
public record RouteUserActionDto(
        @Schema(description = "Идентификатор маршрута, с которым было совершено действие")
        RouteIdDto routeId,
        @Schema(description = "Имя пользователя")
        String userName,
        @Schema(description = "Полное имя пользователя")
        String userDisplayName,
        @Schema(description = "Действие")
        String action,
        @Schema(description = "Момент времени, в который было совершено действие")
        Instant attemptedAt
) {
}
