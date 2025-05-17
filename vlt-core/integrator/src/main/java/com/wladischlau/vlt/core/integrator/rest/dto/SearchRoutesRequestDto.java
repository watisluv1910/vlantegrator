package com.wladischlau.vlt.core.integrator.rest.dto;

import com.wladischlau.vlt.core.integrator.model.SearchRoutesField;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Schema(description = "Запрос на поиск маршрутов")
public record SearchRoutesRequestDto(
        @Schema(description = "Поле, по которому производится поиск",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull
        SearchRoutesField field,
        @Schema(description = "Значение, введённое пользователем в строке поиска",
                requiredMode = Schema.RequiredMode.REQUIRED)
        @NotEmpty
        String query
) {}
