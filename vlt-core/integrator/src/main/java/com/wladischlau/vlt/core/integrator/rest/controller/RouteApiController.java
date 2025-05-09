package com.wladischlau.vlt.core.integrator.rest.controller;

import com.wladischlau.vlt.core.integrator.mapper.DtoMapper;
import com.wladischlau.vlt.core.integrator.rest.api.RouteApi;
import com.wladischlau.vlt.core.integrator.rest.dto.BuildRouteRequestDto;
import com.wladischlau.vlt.core.integrator.rest.dto.CreateRouteRequestDto;
import com.wladischlau.vlt.core.integrator.rest.dto.RouteDto;
import com.wladischlau.vlt.core.integrator.rest.dto.RouteIdDto;
import com.wladischlau.vlt.core.integrator.rest.dto.UpdateRouteDefinitionRequestDto;
import com.wladischlau.vlt.core.integrator.service.RouteBuildService;
import com.wladischlau.vlt.core.integrator.service.VltDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.UUID;

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
    public ResponseEntity<RouteIdDto> createRoute(CreateRouteRequestDto request,
                                                  JwtAuthenticationToken principal) {
        return logRequestProcessingWithResponse(CREATE_ROUTE, () -> {
            var owner = Optional.ofNullable(request.ownerName()).orElse(principal.getName());
            var route = dtoMapper.fromDto(request, owner);
            var id = vltDataService.createRoute(route);
            return ResponseEntity.status(HttpStatus.CREATED).body(dtoMapper.toDto(id));
        });
    }

    @Override
    public ResponseEntity<Void> updateRoute(RouteDto request, JwtAuthenticationToken principal) {
        return logRequestProcessing(UPDATE_ROUTE, () -> {
            vltDataService.updateRoute(dtoMapper.fromDto(request));
            return ResponseEntity.ok().build();
        });
    }

    @Override
    public ResponseEntity<RouteIdDto> updateRouteDefinition(UpdateRouteDefinitionRequestDto request,
                                                            JwtAuthenticationToken principal) {
        return RouteApi.super.updateRouteDefinition(request, principal);
//        return logRequestProcessing(UPDATE_ROUTE_DEFINITION, () -> {
//
//        });
    }

    @Override
    public ResponseEntity<Void> deleteRoute(UUID id, JwtAuthenticationToken principal) {
        return logRequestProcessing(DELETE_ROUTE, () -> {
            // TODO: Delete images and containers for this route
            vltDataService.deleteRouteFullData(id);
            return ResponseEntity.noContent().build();
        });
    }

    @Override
    public ResponseEntity<Void> buildRoute(BuildRouteRequestDto request, JwtAuthenticationToken principal) {
        return logRequestProcessing(BUILD_ROUTE, () -> {
            var def = vltDataService.findRouteDefinitionByRouteId(request.id());
            return def.map(it -> {
                // TODO: Add check if route with the same version hash was already built
                routeBuildService.buildRouteAsync(request.id(), request.versionHash(), it);
                return ResponseEntity.ok().<Void>build();
            }).orElse(ResponseEntity.badRequest().build());
        });
    }
}
