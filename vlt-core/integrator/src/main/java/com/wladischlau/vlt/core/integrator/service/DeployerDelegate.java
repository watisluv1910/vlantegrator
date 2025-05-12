package com.wladischlau.vlt.core.integrator.service;

import com.wladischlau.vlt.core.commons.dto.DeployRequestDto;
import com.wladischlau.vlt.core.commons.model.DeployActionType;
import com.wladischlau.vlt.core.commons.utils.KafkaTopics;
import com.wladischlau.vlt.core.integrator.mapper.DtoMapper;
import com.wladischlau.vlt.core.integrator.model.Route;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class DeployerDelegate {

    private final DtoMapper dtoMapper;
    private final KafkaTemplate<String, DeployRequestDto> deployRequestKafkaTemplate;

    public CompletableFuture<SendResult<String, DeployRequestDto>> sendDeployRequest(Route route, DeployActionType action) {
        var request = dtoMapper.toDto(route, action);
        var requestId = UUID.randomUUID().toString();
        return deployRequestKafkaTemplate.send(KafkaTopics.INTEGRATION_DEPLOY_REQUEST, requestId, request);
    }
}
