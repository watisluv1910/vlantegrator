package com.wladischlau.vlt.core.integrator.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.NotEmpty;

@Schema(description = "Docker-сеть")
public record DockerNetworkDto(
        @Schema(description = "Имя сети", requiredMode = RequiredMode.REQUIRED)
        @NotEmpty String name,
        @Schema(description = "Драйвер сети", requiredMode = RequiredMode.REQUIRED)
        @NotEmpty String driver
) {
}
