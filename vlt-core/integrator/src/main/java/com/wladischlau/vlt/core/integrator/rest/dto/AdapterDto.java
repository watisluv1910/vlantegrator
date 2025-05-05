package com.wladischlau.vlt.core.integrator.rest.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "Описание адаптера")
public record AdapterDto(
        @Schema(description = "Идентификатор адаптера")
        UUID id,
        @Schema(description = "Название адаптера")
        String name,
        @Schema(description = "Отображаемое название адаптера")
        String displayName,
        @Schema(description = "Описание адаптера")
        String description,
        @Schema(description = "Полное имя класса адаптера")
        String className,
        @Schema(description = "Тип адаптера")
        String type,
        @Schema(description = "Направление (IN/OUT/COMMON)")
        String direction,
        @Schema(description = "Тип канала (CHANNEL/GATEWAY/NONE)")
        String channelKind
) {
}
