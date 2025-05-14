package com.wladischlau.vlt.core.integrator.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "Описание адаптера")
public record AdapterDto(
        @Schema(description = "Идентификатор адаптера", requiredMode = Schema.RequiredMode.REQUIRED)
        UUID id,
        @Schema(description = "Название адаптера", requiredMode = Schema.RequiredMode.REQUIRED)
        String name,
        @Schema(description = "Отображаемое название адаптера", requiredMode = Schema.RequiredMode.REQUIRED)
        String displayName,
        @Schema(description = "Описание адаптера", requiredMode = Schema.RequiredMode.REQUIRED)
        String description,
        @Schema(description = "Полное имя класса адаптера", requiredMode = Schema.RequiredMode.REQUIRED)
        String className,
        @Schema(description = "Тип адаптера", requiredMode = Schema.RequiredMode.REQUIRED)
        String type,
        @Schema(description = "Направление (IN/OUT/COMMON)", requiredMode = Schema.RequiredMode.REQUIRED)
        String direction,
        @Schema(description = "Тип канала (CHANNEL/GATEWAY/NONE)", requiredMode = Schema.RequiredMode.REQUIRED)
        String channelKind
) {
}
