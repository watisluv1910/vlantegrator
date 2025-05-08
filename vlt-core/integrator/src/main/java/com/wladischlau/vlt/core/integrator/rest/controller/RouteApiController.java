package com.wladischlau.vlt.core.integrator.rest.controller;

import com.wladischlau.vlt.core.integrator.mapper.DtoMapper;
import com.wladischlau.vlt.core.integrator.rest.api.RouteApi;
import com.wladischlau.vlt.core.integrator.rest.dto.BuildRouteRequestDto;
import com.wladischlau.vlt.core.integrator.service.RouteBuildService;
import com.wladischlau.vlt.core.integrator.service.VltDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class RouteApiController extends ApiController implements RouteApi {

    private final VltDataService vltDataService;
    private final RouteBuildService routeBuildService;

    public RouteApiController(DtoMapper dtoMapper, VltDataService vltDataService, RouteBuildService routeBuildService) {
        super(dtoMapper);
        this.vltDataService = vltDataService;
        this.routeBuildService = routeBuildService;
    }

    @Override
    public ResponseEntity<Void> buildRoute(BuildRouteRequestDto request, JwtAuthenticationToken principal) {
        return logRequestProcessing(BUILD_ROUTE, () -> {
            var def = vltDataService.findRouteDefinitionByRouteId(request.routeId());
            return def.map(it -> {
                // TODO: Add check if route with the same version hash was already built
                routeBuildService.buildRouteAsync(request.routeId(), request.versionHash(), it);
                return ResponseEntity.ok().<Void>build();
            }).orElse(ResponseEntity.badRequest().build());
        });
    }
}
