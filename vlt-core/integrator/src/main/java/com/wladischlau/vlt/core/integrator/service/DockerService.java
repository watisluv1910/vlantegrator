package com.wladischlau.vlt.core.integrator.service;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Container;
import com.github.dockerjava.api.model.Network;
import com.wladischlau.vlt.core.commons.model.ContainerStatus;
import com.wladischlau.vlt.core.commons.model.RouteId;
import com.wladischlau.vlt.core.integrator.model.DockerNetwork;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class DockerService {

    private final DockerClient docker;

    public List<DockerNetwork> getNetworks() {
        List<Network> nets = docker.listNetworksCmd().exec();
        return nets.stream()
                .map(n -> new DockerNetwork(n.getName(), n.getDriver()))
                .collect(Collectors.toList());
    }

    public DockerNetwork createNetwork(String name, String driver) {
        var resp = docker.createNetworkCmd()
                .withName(name)
                .withDriver(driver)
                .exec();

        var created = docker.inspectNetworkCmd()
                .withNetworkId(resp.getId())
                .exec();

        return new DockerNetwork(created.getName(), created.getDriver());
    }

    public Map<String, ContainerStatus> getRouteStatuses(List<RouteId> routeIds) {
        var containers = docker.listContainersCmd()
                .withShowAll(true) // Include stopped containers
                .exec();

        var result = new HashMap<String, ContainerStatus>();

        routeIds.stream().map(RouteId::full)
                .forEach(routeId -> {
                    var status = containers.stream()
                            .filter(it -> Arrays.stream(it.getNames())
                                    .anyMatch(name -> name.contains(routeId)))
                            .findFirst()
                            .map(Container::getStatus)
                            .map(ContainerStatus::fromLiteral)
                            .orElse(ContainerStatus.UNDEFINED);

                    result.put(routeId, status);
                });

        return result;
    }
}
