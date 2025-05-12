package com.wladischlau.vlt.core.integrator.service;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallbackTemplate;
import com.github.dockerjava.api.command.InspectContainerResponse;
import com.github.dockerjava.api.exception.NotFoundException;
import com.github.dockerjava.api.model.CpuUsageConfig;
import com.github.dockerjava.api.model.Statistics;
import com.wladischlau.vlt.core.commons.model.ContainerHealthStatus;
import com.wladischlau.vlt.core.integrator.model.PlatformBasicHealth;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.Closeable;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlatformHealthService {

    private static final String VLT_CONTAINER_PREFIX = "vlt-";

    private static final String VLT_DB_CONTAINER = "vlt-core-db";
    private static final String VLT_KAFKA_CONTAINER_1 = "vlt-kafka.broker_1";
    private static final String VLT_KAFKA_CONTAINER_2 = "vlt-kafka.broker_2";

    private final DockerClient docker;

    private final Map<String, StatsHolder> statsCache = new ConcurrentHashMap<>();
    private final Map<String, Closeable> streamHandle = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        docker.listContainersCmd().withShowAll(false).exec().forEach(c -> {
            String name = c.getNames()[0].replaceFirst("^/", "");
            if (name.startsWith(VLT_CONTAINER_PREFIX))
                startStream(c.getId());
        });
    }

    @PreDestroy
    public void destroy() throws IOException {
        var streams = streamHandle.values();
        for (var c : streams) {
            c.close();
        }
    }

    public PlatformBasicHealth getCurrentHealth() {
        double totalCpuPercent = 0.0;
        long totalMemUsed = 0L;
        long totalMemLimit = 0L;

        for (StatsHolder holder : statsCache.values()) {
            totalCpuPercent += holder.getCpuPercent();
            totalMemUsed += holder.getMemUsed();
            totalMemLimit = Math.max(totalMemLimit, holder.getMemLimit());
        }

        var dbHealth = getHealthStatus(VLT_DB_CONTAINER);
        var kafkaHealth = healthier(getHealthStatus(VLT_KAFKA_CONTAINER_1), getHealthStatus(VLT_KAFKA_CONTAINER_2));
        return new PlatformBasicHealth(totalCpuPercent, totalMemUsed, totalMemLimit, dbHealth, kafkaHealth);
    }

    private void startStream(String id) {
        var h = docker.statsCmd(id)
                .exec(new ResultCallbackTemplate<>() {
                    @Override
                    public void onNext(Statistics s) {
                        statsCache.computeIfAbsent(id, k -> new StatsHolder()).update(s);
                    }

                    @Override
                    public void onError(Throwable t) {
                        log.warn("Stats stream for {} failed: {}", id, t.getMessage());
                    }
                });

        streamHandle.put(id, h);
    }

    private ContainerHealthStatus getHealthStatus(String name) {
        try {
            InspectContainerResponse r = docker.inspectContainerCmd(name).exec();
            var health = r.getState().getHealth();
            if (health != null) {
                return switch (health.getStatus()) {
                    case "healthy" -> ContainerHealthStatus.HEALTHY;
                    case "starting" -> ContainerHealthStatus.STARTING;
                    case "unhealthy" -> ContainerHealthStatus.UNHEALTHY;
                    default -> ContainerHealthStatus.UNKNOWN;
                };
            }

            var containerStatus = Optional.ofNullable(r.getState().getStatus()).orElse("");
            return switch (containerStatus) {
                case "running" -> ContainerHealthStatus.HEALTHY;
                case "restarting", "paused", "created" -> ContainerHealthStatus.STARTING;
                case "exited", "dead" -> ContainerHealthStatus.UNHEALTHY;
                default -> ContainerHealthStatus.UNKNOWN;
            };
        } catch (NotFoundException e) {
            return ContainerHealthStatus.UNKNOWN;
        }
    }

    private static ContainerHealthStatus healthier(ContainerHealthStatus a, ContainerHealthStatus b) {
        if (a == ContainerHealthStatus.HEALTHY || b == ContainerHealthStatus.HEALTHY)
            return ContainerHealthStatus.HEALTHY;
        if (a == ContainerHealthStatus.STARTING || b == ContainerHealthStatus.STARTING)
            return ContainerHealthStatus.STARTING;
        if (a == ContainerHealthStatus.UNHEALTHY || b == ContainerHealthStatus.UNHEALTHY)
            return ContainerHealthStatus.UNHEALTHY;
        return ContainerHealthStatus.UNKNOWN;
    }

    @Getter
    private static final class StatsHolder {

        private volatile double cpuPercent;
        private volatile long memUsed;
        private volatile long memLimit;

        void update(Statistics s) {
            Long curTotal = get(Optional.ofNullable(s.getCpuStats().getCpuUsage()).map(CpuUsageConfig::getTotalUsage).orElse(null));
            Long prevTotal = get(Optional.ofNullable(s.getPreCpuStats().getCpuUsage()).map(CpuUsageConfig::getTotalUsage).orElse(null));
            Long curSystem = get(s.getCpuStats().getSystemCpuUsage());
            Long prevSystem = get(s.getPreCpuStats().getSystemCpuUsage());
            Long online = s.getCpuStats().getOnlineCpus();

            if (curTotal != null && prevTotal != null
                    && curSystem != null && prevSystem != null
                    && curTotal > prevTotal && curSystem > prevSystem
                    && online != null && online > 0) {
                long deltaTotal = curTotal - prevTotal;
                long deltaSystem = curSystem - prevSystem;
                this.cpuPercent = (deltaTotal * online * 100.0) / deltaSystem;
            }

            Long used = get(s.getMemoryStats().getUsage());
            Long limit = get(s.getMemoryStats().getLimit());
            if (used != null && limit != null) {
                this.memUsed = used;
                this.memLimit = limit;
            }
        }

        private static Long get(Long value) {
            return value == null || value == 0L ? null : value;
        }
    }
}