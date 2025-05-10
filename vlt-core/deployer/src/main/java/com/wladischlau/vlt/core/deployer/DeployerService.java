package com.wladischlau.vlt.core.deployer;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.ExposedPort;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.Network;
import com.github.dockerjava.api.model.Ports;
import com.wladischlau.vlt.core.commons.model.deploy.DeployActionType;
import com.wladischlau.vlt.core.commons.dto.DeployRequestDto;
import com.wladischlau.vlt.core.commons.dto.DeployStatusDto;
import com.wladischlau.vlt.core.commons.model.kafka.KafkaStatusCode;
import com.wladischlau.vlt.core.deployer.config.DockerClientProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeployerService {

    public static final Integer ROUTE_STOP_TIMEOUT_SECONDS = 20;
    public static final String ROUTE_CONTAINER_NAME_PREFIX = "vlt-route-";
    public static final List<String> ROUTE_CONTAINER_DEFAULT_NETWORKS = List.of("monitoring");

    public static final String INTEGRATION_DEPLOY_REQUEST_TOPIC = "integration.deploy.request";
    public static final String INTEGRATION_DEPLOY_STATUS_TOPIC = "integration.deploy.status";

    private final DockerClient docker;
    private final DockerClientProperties dockerProperties;
    private final KafkaTemplate<String, DeployStatusDto> statusProducer;

    @KafkaListener(topics = INTEGRATION_DEPLOY_REQUEST_TOPIC, containerFactory = "deployRequestFactory")
    public void handleDeployRequest(@Payload DeployRequestDto request) {
        log.info("Получен запрос [route: {}, action: {}]", request.routeId().full(), request.action());
        switch (request.action()) {
            case START -> handleStart(request);
            case STOP -> handleStop(request);
            case DELETE -> handleDelete(request);
            case RESTART -> handleRestart(request);
        }
    }

    private void handleStart(DeployRequestDto req) {
        var containerName = toRouteContainerName(req.routeId().full());
        var container = docker.listContainersCmd().withShowAll(true).exec().stream()
                .filter(c -> Arrays.asList(c.getNames()).contains("/" + containerName))
                .findFirst();

        container.ifPresentOrElse(
                it -> {
                    if (it.getState().equalsIgnoreCase("running")) {
                        var msg = MessageFormat.format("Маршрут уже запущен [route: {0}]", req.routeId().full());
                        log.error(msg);
                        sendStatus(req, KafkaStatusCode.ERROR, msg);
                    } else { // Контейнер есть, но не запущен
                        log.debug("Обнаружен контейнер: {} в состоянии: {}", containerName, it.getState());
                        try {
                            docker.startContainerCmd(it.getId()).exec();
                            var msg = MessageFormat.format("Контейнер запущен [id: {0}, name: {1}]",
                                                           it.getId(), containerName);
                            log.info(msg);
                            sendStatus(req, KafkaStatusCode.OK, msg);
                        } catch (Exception e) {
                            var msg = MessageFormat.format(
                                    "Ошибка при запуске контейнера для маршрута [containerId: {0}, route {1}, ex: {2}]",
                                    it.getId(), req.routeId().full(), e.getMessage());
                            log.error(msg, e);
                            sendStatus(req, KafkaStatusCode.ERROR, msg);
                        }
                    }
                },
                () -> createAndStartContainer(containerName, req)
        );
    }

    private void createAndStartContainer(String containerName, DeployRequestDto req) {
        try {
            var image = toRouteImageName(req.routeId().full());
            pullImageIfNeeded(image);

            var defaultLabels = new HashMap<String, String>();
            defaultLabels.put("routeUuid", req.routeId().id().toString());
            defaultLabels.put("commitHash", req.routeId().versionHash());

            var createCmd = docker.createContainerCmd(image)
                    .withName(containerName)
                    .withEnv(convertEnv(req.env()));

            if (req.ports() != null) {
                var portBinding = new Ports();
                ExposedPort exposedPort;
                var split = req.ports().split(":");
                if (split.length != 2) {
                    var msg = MessageFormat.format(
                            "Ошибка в конфигурации маршрута. Неверный формат маппинга портов [route: {0}, mapping: {1}]",
                            req.routeId().full(), req.ports());
                    log.error(msg);
                    throw new IllegalArgumentException(msg);
                }

                try {
                    int hostPort = Integer.parseInt(split[0]);
                    int containerPort = Integer.parseInt(split[1]);

                    exposedPort = ExposedPort.tcp(containerPort);
                    portBinding.bind(exposedPort, Ports.Binding.bindPort(hostPort));
                } catch (NumberFormatException e) {
                    var msg = MessageFormat.format(
                            "Ошибка в конфигурации маршрута. Порт должен быть числом [route: {0}, port: {1}]",
                            req.routeId().full(), req.ports());
                    log.error(msg);
                    throw new IllegalArgumentException(msg);
                }

                defaultLabels.put("metrics", Boolean.TRUE.toString());
                defaultLabels.put("metrics_port", String.valueOf(exposedPort.getPort()));

                createCmd = createCmd.withExposedPorts(List.of(exposedPort))
                        .withHostConfig(HostConfig.newHostConfig().withPortBindings(portBinding));
            }

            createCmd = createCmd.withLabels(defaultLabels);

            var containerId = createCmd.exec().getId();

            Stream.concat(ROUTE_CONTAINER_DEFAULT_NETWORKS.stream(), req.networks().stream())
                    .distinct()
                    .forEach(network -> connectToNetwork(network, containerId));
            log.info("Контейнер подключён к сетям [id: {}, container: {}, networks: {}]",
                      containerId, containerName, req.networks());

            docker.startContainerCmd(containerId).exec();
            var msg = MessageFormat.format("Контейнер запущен [id: {0}, name: {1}]", containerId, containerName);
            log.info(msg);
            sendStatus(req, KafkaStatusCode.OK, msg);
        } catch (Exception e) {
            var msg = MessageFormat.format("Ошибка при создании контейнера для маршрута [route {0}, ex: {1}]",
                                           req.routeId().full(), e.getMessage());
            log.error(msg, e);
            sendStatus(req, KafkaStatusCode.ERROR, msg);
        }
    }

    // TODO: Отдельный запрос из модуля Integrator на предварительное создание сети
    private void connectToNetwork(String netName, String containerId) {
        try {
            var networkId = docker.listNetworksCmd().exec().stream()
                    .filter(net -> net.getName().equals(netName))
                    .map(Network::getId)
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("Сеть " + netName + " не определена"));

            docker.connectToNetworkCmd()
                    .withContainerId(containerId)
                    .withNetworkId(networkId)
                    .exec();

            log.debug("Контейнер подключён к сети [id: {}, network: {}]", containerId, netName);
        } catch (Exception e) {
            log.error("Невозможно подключить контейнер к сети [id: {}, network: {}, ex: {}]",
                      containerId, netName, e.getMessage());
            throw e;
        }
    }

    private void handleStop(DeployRequestDto req) {
        var containerName = toRouteContainerName(req.routeId().full());
        var cont = findContainer(containerName);

        if (cont == null) {
            var msg = MessageFormat.format("Контейнер не найден [route: {0}]", req.routeId().full());
            log.warn(msg);
            sendStatus(req, KafkaStatusCode.OK, msg);
            return;
        }

        try {
            docker.stopContainerCmd(cont.getId()).withTimeout(ROUTE_STOP_TIMEOUT_SECONDS).exec();
            var msg = MessageFormat.format("Маршрут остановлен [route: {0}]", req.routeId().full());
            log.info(msg);
            sendStatus(req, KafkaStatusCode.OK, msg);
        } catch (Exception e) {
            var msg = MessageFormat.format("Ошибка при остановке контейнера [name: {0}, error: {1}]",
                                           containerName, e.getMessage());
            log.error(msg, e);
            sendStatus(req, KafkaStatusCode.ERROR, msg);
        }
    }

    private void handleDelete(DeployRequestDto req) {
        handleStop(req);

        var containerName = toRouteContainerName(req.routeId().full());
        var cont = findContainer(containerName);

        if (cont == null) {
            var msg = MessageFormat.format("Контейнер не найден (уже удалён?) [route: {0}]", req.routeId().full());
            log.warn(msg);
            sendStatus(req, KafkaStatusCode.OK, msg);
            return;
        }
        try {
            docker.removeContainerCmd(cont.getId()).withForce(true).exec();
            var msg = MessageFormat.format("Контейнер удалён [id: {0}, name: {1}]", cont.getId(), containerName);
            log.info(msg);
            sendStatus(req, KafkaStatusCode.OK, msg);
        } catch (Exception e) {
            var msg = MessageFormat.format("Ошибка при удалении контейнера [name: {0}, error: {1}]",
                                           containerName, e.getMessage());
            log.error(msg, e);
            sendStatus(req, KafkaStatusCode.ERROR, msg);
        }
    }

    private void handleRestart(DeployRequestDto req) {
        // restart = stop + start
        handleStop(req);
        handleStart(req);
    }

    private Container findContainer(String containerName) {
        return docker.listContainersCmd().withShowAll(true).exec().stream()
                .filter(c -> Arrays.asList(c.getNames()).contains("/" + containerName))
                .findFirst()
                .orElse(null);
    }

    private void sendStatus(DeployRequestDto req, KafkaStatusCode statusCode, String msg) {
        var status = DeployStatusDto.builder()
                .routeId(req.routeId())
                .status(statusCode)
                .action(DeployActionType.valueOf(req.action().name()))
                .message(msg)
                .timestamp(ZonedDateTime.now(ZoneOffset.UTC))
                .build();

        statusProducer.send(INTEGRATION_DEPLOY_STATUS_TOPIC, status);
        log.debug("Отослан статус развёртывания [route: {}, status: {}]", req.routeId().full(), statusCode);
    }

    private void pullImageIfNeeded(String imageName) throws InterruptedException {
        boolean pulled = docker.listImagesCmd().withShowAll(true).exec()
                .stream()
                .flatMap(image -> Arrays.stream(image.getRepoTags()))
                .anyMatch(imageName::contains);

        if (pulled) {
            log.info("Образ {} уже доступен локально, пропуск загрузки...", imageName);
            return;
        }

        log.debug("Загрузка образа: {}", imageName);
        // imageName is watisluv/vlt-routes-registry/<route_uuid>.<commit_hash>
        docker.pullImageCmd(imageName).start().awaitCompletion();
        log.info("Образ {} загружен", imageName);
    }

    private List<String> convertEnv(Map<String, Object> envMap) {
        return Optional.ofNullable(envMap)
                .map(Map::entrySet)
                .map(entries -> entries.stream()
                        .map(e -> e.getKey() + "=" + e.getValue())
                        .toList())
                .orElse(Collections.emptyList());
    }

    private String toRouteContainerName(String fullId) {
        return ROUTE_CONTAINER_NAME_PREFIX + fullId;
    }

    private String toRouteImageName(String tag) {
        return dockerProperties.imageRegistry() + ":" + tag;
    }
}