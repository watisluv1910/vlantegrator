package com.wladischlau.vlt.core.integrator.rest.dto;

import com.wladischlau.vlt.core.commons.dto.RouteIdDto;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

import java.time.Instant;

@Schema(description = "Описание действия пользователя")
public record RouteUserActionDto(
        @Schema(description = "Идентификатор маршрута, с которым было совершено действие",
                requiredMode = RequiredMode.REQUIRED)
        RouteIdDto routeId,
        @Schema(description = "Имя пользователя",
                requiredMode = RequiredMode.REQUIRED)
        String userName,
        @Schema(description = "Полное имя пользователя",
                requiredMode = RequiredMode.REQUIRED)
        String userDisplayName,
        @Schema(description = "Действие",
                requiredMode = RequiredMode.REQUIRED)
        String action,
        @Schema(description = "Момент времени, в который было совершено действие",
                requiredMode = RequiredMode.REQUIRED)
        Instant attemptedAt
) {
}
