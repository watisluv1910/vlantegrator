package com.wladischlau.vlt.core.integrator.rest.controller;

import com.wladischlau.vlt.core.integrator.mapper.DtoMapper;
import com.wladischlau.vlt.core.integrator.rest.api.HealthApi;
import com.wladischlau.vlt.core.integrator.rest.dto.PlatformBasicHealthDto;
import com.wladischlau.vlt.core.integrator.service.PlatformHealthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class HealthApiController extends ApiController implements HealthApi {

    private final PlatformHealthService platformHealthService;

    public HealthApiController(DtoMapper dtoMapper, PlatformHealthService platformHealthService) {
        super(dtoMapper);
        this.platformHealthService = platformHealthService;
    }

    @Override
    public ResponseEntity<PlatformBasicHealthDto> getBasicHealth(JwtAuthenticationToken principal) {
        return logRequestProcessing(GET_BASIC_HEALTH, () -> {
            var health = dtoMapper.toDto(platformHealthService.getCurrentHealth());
            return ResponseEntity.ok(health);
        });
    }
}
