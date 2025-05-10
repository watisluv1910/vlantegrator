package com.wladischlau.vlt.core.integrator.rest.controller;

import com.wladischlau.vlt.core.commons.dto.RouteIdDto;
import com.wladischlau.vlt.core.commons.model.RouteId;
import com.wladischlau.vlt.core.integrator.mapper.DtoMapper;
import com.wladischlau.vlt.core.integrator.rest.api.RouteApi;
import com.wladischlau.vlt.core.integrator.rest.dto.BuildRouteRequestDto;
import com.wladischlau.vlt.core.integrator.rest.dto.CreateRouteRequestDto;
import com.wladischlau.vlt.core.integrator.rest.dto.RouteDefinitionDto;
import com.wladischlau.vlt.core.integrator.rest.dto.RouteDto;
import com.wladischlau.vlt.core.integrator.service.RouteBuildService;
import com.wladischlau.vlt.core.integrator.service.VltDataService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.RestController;

import java.text.MessageFormat;
import java.util.NoSuchElementException;
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
    public ResponseEntity<RouteDefinitionDto> getRouteDefinition(UUID id, String versionHash) {
        return logRequestProcessing(GET_ROUTE_DEFINITION, () -> {
            var routeId = new RouteId(id, versionHash);
            return vltDataService.findRouteCacheData(routeId)
                    .map(cache -> dtoMapper.toDto(cache, routeId))
                    .map(ResponseEntity::ok)
                    .orElseThrow(() -> {
                        var msg = MessageFormat.format("Route definition not found [id: {0}]", routeId);
                        log.error(msg);
                        return new NoSuchElementException(msg);
                    });
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
    public ResponseEntity<RouteIdDto> updateRouteDefinition(RouteDefinitionDto request,
                                                            JwtAuthenticationToken principal) {
        return logRequestProcessing(UPDATE_ROUTE_DEFINITION, () -> {
            var nodes = request.nodes().stream()
                    .map(nodeDto -> vltDataService.findAdapterById(nodeDto.adapterId())
                            .map(adapter -> dtoMapper.fromDto(nodeDto, adapter))
                            .orElseThrow(() -> {
                                var msg = MessageFormat.format("Adapter with not found [id: {0}]", nodeDto.adapterId());
                                log.error(msg);
                                return new NoSuchElementException(msg);
                            }))
                    .toList();
            var connections = dtoMapper.fromDtoToConnectionsFullData(request.connections());
            var routeId = dtoMapper.fromDto(request.id());
            var newRouteId = vltDataService.updateRouteDefinition(routeId, nodes, connections);
            return ResponseEntity.ok().body(dtoMapper.toDto(newRouteId));
        });
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
            var def = vltDataService.findLatestRouteDefinitionByRouteId(request.id());
            return def.map(it -> {
                // TODO: Add check if route with the same version hash was already built
                routeBuildService.buildRouteAsync(request.id(), request.versionHash(), it);
                return ResponseEntity.ok().<Void>build();
            }).orElse(ResponseEntity.badRequest().build());
        });
    }
}
