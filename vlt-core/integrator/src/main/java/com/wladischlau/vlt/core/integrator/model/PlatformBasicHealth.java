package com.wladischlau.vlt.core.integrator.model;

import com.wladischlau.vlt.core.commons.model.ContainerHealthStatus;

public record PlatformBasicHealth(double cpuPercent, long memUsed, long memTotal,
                                  ContainerHealthStatus dbStatus, ContainerHealthStatus kafkaStatus) {}