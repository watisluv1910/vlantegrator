package com.wladischlau.vlt.core.integrator.service;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Network;
import com.wladischlau.vlt.core.integrator.model.DockerNetwork;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
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
}
