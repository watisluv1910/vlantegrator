package com.wladischlau.vlt.core.integrator.rest.dto;

import com.wladischlau.vlt.core.commons.model.ContainerHealthStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;

@Schema(description = "Основная информация о состоянии платформы")
public record PlatformBasicHealthDto(
        @Schema(description = "Процент используемых ресурсов CPU",
                requiredMode = RequiredMode.REQUIRED)
        double cpuPercent,
        @Schema(description = "Используемая оперативная память в байтах",
                requiredMode = RequiredMode.REQUIRED)
        long memUsedBytes,
        @Schema(description = "Общий объём выделенной памяти на клиент",
                requiredMode = RequiredMode.REQUIRED)
        long memTotalBytes,
        @Schema(description = "Статус БД ядра",
                requiredMode = RequiredMode.REQUIRED)
        ContainerHealthStatus dbStatus,
        @Schema(description = "Статус Kafka",
                requiredMode = RequiredMode.REQUIRED)
        ContainerHealthStatus kafkaStatus) {
}