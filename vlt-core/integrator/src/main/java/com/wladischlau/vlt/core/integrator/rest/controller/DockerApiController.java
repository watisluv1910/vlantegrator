package com.wladischlau.vlt.core.integrator.rest.controller;

import com.wladischlau.vlt.core.integrator.mapper.DtoMapper;
import com.wladischlau.vlt.core.integrator.rest.api.DockerApi;
import com.wladischlau.vlt.core.integrator.rest.dto.DockerNetworkDto;
import com.wladischlau.vlt.core.integrator.service.DockerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
public class DockerApiController extends ApiController implements DockerApi {

    private final DockerService dockerService;

    public DockerApiController(DtoMapper dtoMapper, DockerService dockerService) {
        super(dtoMapper);
        this.dockerService = dockerService;
    }

    @Override
    public ResponseEntity<Void> createNetwork(DockerNetworkDto request, JwtAuthenticationToken principal) {
        return logRequestProcessing(CREATE_DOCKER_NETWORK, () -> {
            if (request.name().isBlank() || request.driver().isBlank()) {
                return ResponseEntity.badRequest().build();
            }
            var created = dockerService.createNetwork(request.name(), request.driver());
            log.info("Created Docker-network: {}", created);
            return ResponseEntity.status(HttpStatus.CREATED).build();
        });
    }

    @Override
    public ResponseEntity<List<DockerNetworkDto>> getAvailableNetworks(JwtAuthenticationToken principal) {
        return logRequestProcessing(GET_DOCKER_NETWORKS, () -> {
            return ResponseEntity.ok(dtoMapper.toDtoFromDockerNetwork(dockerService.getNetworks()));
        });
    }
}
