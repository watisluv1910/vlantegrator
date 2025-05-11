package com.wladischlau.vlt.core.integrator.rest.controller;

import com.wladischlau.vlt.core.commons.dto.RouteIdDto;
import com.wladischlau.vlt.core.commons.model.RouteId;
import com.wladischlau.vlt.core.commons.model.deploy.DeployActionType;
import com.wladischlau.vlt.core.integrator.mapper.DtoMapper;
import com.wladischlau.vlt.core.integrator.rest.api.RouteApi;
import com.wladischlau.vlt.core.integrator.rest.dto.CreateRouteRequestDto;
import com.wladischlau.vlt.core.integrator.rest.dto.RouteDefinitionDto;
import com.wladischlau.vlt.core.integrator.rest.dto.RouteDto;
import com.wladischlau.vlt.core.integrator.rest.dto.UpdateRouteRequestDto;
import com.wladischlau.vlt.core.integrator.service.DeployerDelegate;
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
    private final DeployerDelegate deployerDelegate;

    public RouteApiController(DtoMapper dtoMapper,
                              VltDataService vltDataService,
                              RouteBuildService routeBuildService,
                              DeployerDelegate deployerDelegate) {
        super(dtoMapper);
        this.vltDataService = vltDataService;
        this.routeBuildService = routeBuildService;
        this.deployerDelegate = deployerDelegate;
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
    public ResponseEntity<RouteDto> getRoute(UUID id) {
        return logRequestProcessing(GET_ROUTE, () -> {
            return vltDataService.findRouteById(id)
                    .map(dtoMapper::toDto)
                    .map(ResponseEntity::ok)
                    .orElseThrow(() -> {
                        var msg = MessageFormat.format("Route information not found [id: {0}]", id);
                        log.error(msg);
                        return new NoSuchElementException(msg);
                    });
        });
    }

    @Override
    public ResponseEntity<RouteDefinitionDto> getRouteDefinition(UUID id, String versionHash) {
        return logRequestProcessing(GET_ROUTE_DEFINITION, () -> {
            var routeId = new RouteId(id, versionHash);
            return vltDataService.findRouteCacheData(routeId)
                    .map(dtoMapper::toDto)
                    .map(ResponseEntity::ok)
                    .orElseThrow(() -> {
                        var msg = MessageFormat.format("Route definition not found [id: {0}]", routeId);
                        log.error(msg);
                        return new NoSuchElementException(msg);
                    });
        });
    }

    @Override
    public ResponseEntity<Void> updateRoute(UUID routeId, UpdateRouteRequestDto request,
                                            JwtAuthenticationToken principal) {
        // TODO: Add owner check
        return logRequestProcessing(UPDATE_ROUTE, () -> {
            vltDataService.updateRoute(dtoMapper.fromDto(request, routeId));
            return ResponseEntity.ok().build();
        });
    }

    @Override
    public ResponseEntity<RouteIdDto> updateRouteDefinition(UUID id,
                                                            String versionHash,
                                                            RouteDefinitionDto request,
                                                            JwtAuthenticationToken principal) {
        // TODO: Add owner check
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
            var routeId = new RouteId(id, versionHash);
            var newRouteId = vltDataService.updateRouteDefinition(routeId, nodes, connections);
            return ResponseEntity.ok().body(dtoMapper.toDto(newRouteId));
        });
    }

    @Override
    public ResponseEntity<Void> deleteRoute(UUID id, JwtAuthenticationToken principal) {
        // TODO: Add owner check
        return logRequestProcessing(DELETE_ROUTE, () -> {
            // TODO: Delete images and containers for this route
            vltDataService.deleteRouteFullData(id);
            return ResponseEntity.noContent().build();
        });
    }

    @Override
    public ResponseEntity<Void> buildRoute(UUID id, JwtAuthenticationToken principal) {
        return logRequestProcessing(BUILD_ROUTE, () -> {
            var def = vltDataService.findLatestRouteDefinitionByRouteId(id);
            var versionHash = vltDataService.findRouteById(id) // Находит последний versionHash
                    .map(it -> it.routeId().versionHash())
                    .orElseThrow(() -> {
                        var msg = MessageFormat.format("Unable to find route version hash [id: {0}]", id);
                        log.error(msg);
                        return new IllegalStateException(msg);
                    });

            return def.map(it -> {
                // TODO: Add check if route with the same version hash was already built
                routeBuildService.buildRouteAsync(id, versionHash, it);
                return ResponseEntity.ok().<Void>build();
            }).orElse(ResponseEntity.badRequest().build());
        });
    }

    @Override
    public ResponseEntity<Void> deployRoute(UUID id, String action, JwtAuthenticationToken principal) {
        return logRequestProcessing(DEPLOY_ROUTE, () -> {
            var actionType = DeployActionType.valueOf(action.trim().toUpperCase());
            vltDataService.findRouteById(id)
                    .ifPresent(it -> deployerDelegate.sendDeployRequest(it, actionType));
            return ResponseEntity.ok().build();
        });
    }
}
