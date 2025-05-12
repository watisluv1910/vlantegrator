package com.wladischlau.vlt.core.integrator.rest.dto;

import com.wladischlau.vlt.core.commons.model.ContainerHealthStatus;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Основная информация о состоянии платформы")
public record PlatformBasicHealthDto(
        @Schema(description = "Процент используемых ресурсов CPU")
        double cpuPercent,
        @Schema(description = "Используемая оперативная память в байтах")
        long memUsedBytes,
        @Schema(description = "Общий объём выделенной памяти на клиент")
        long memTotalBytes,
        @Schema(description = "Статус БД ядра")
        ContainerHealthStatus dbStatus,
        @Schema(description = "Статус Kafka")
        ContainerHealthStatus kafkaStatus) {
}