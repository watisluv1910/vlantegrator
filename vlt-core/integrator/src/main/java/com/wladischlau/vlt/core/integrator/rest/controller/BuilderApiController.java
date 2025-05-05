package com.wladischlau.vlt.core.integrator.rest.controller;

import com.wladischlau.vlt.core.integrator.mapper.DtoMapper;
import com.wladischlau.vlt.core.integrator.rest.api.BuilderApi;
import com.wladischlau.vlt.core.integrator.rest.dto.BuildRouteRequestDto;
import com.wladischlau.vlt.core.integrator.service.RouteBuildService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class BuilderApiController extends ApiController implements BuilderApi {

    private final RouteBuildService routeBuildService;

    public BuilderApiController(DtoMapper dtoMapper, RouteBuildService routeBuildService) {
        super(dtoMapper);
        this.routeBuildService = routeBuildService;
    }

    @Override
    public ResponseEntity<Void> buildRoute(BuildRouteRequestDto request, JwtAuthenticationToken principal) {
        return logRequestProcessing(BUILD_ROUTE, () -> {
            routeBuildService.buildRouteAsync(request.routeId(), request.versionHash(), null); // TODO: Rework
            return ResponseEntity.ok().build();
        });
    }
}
