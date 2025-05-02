package com.wladischlau.vlt.core.integrator.rest.controller;

import com.wladischlau.vlt.core.integrator.model.Route;
import com.wladischlau.vlt.core.integrator.rest.dto.BuildRouteRequestDto;
import com.wladischlau.vlt.core.integrator.service.RouteBuildService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class RouteController {

    private static final String BUILD_ROUTE = "buildRoute";

    private final RouteBuildService routeBuildService;

    @Operation(
            security = @SecurityRequirement(name = "bearerAuth"),
            operationId = BUILD_ROUTE,
            summary = "Асинхронная сборка маршрута",
            description = "Принимает описание маршрута в DTO и запускает процесс сборки в фоновом режиме.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Запрос на сборку принят"),
                    @ApiResponse(responseCode = "400", description = "Bad request",
                            content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
                    @ApiResponse(responseCode = "401", description = "Unauthorized",
                            content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
                    @ApiResponse(responseCode = "403", description = "Forbidden",
                            content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
                    @ApiResponse(responseCode = "500", description = "Server error",
                            content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            }
    )
    @Tag(name = "routes")
    @PostMapping("/v1/route/build")
    public ResponseEntity<?> buildRoute(@RequestBody(required = true,
                                                content = @Content(schema = @Schema(implementation = BuildRouteRequestDto.class)))
                                        @org.springframework.web.bind.annotation.RequestBody BuildRouteRequestDto request,
                                        JwtAuthenticationToken ignored) {
        log.info("Получен запрос на сборку маршрута: {}", request);
        var route = convertToRoute(request);
        routeBuildService.buildRouteAsync(request.routeId(), request.commitHash(), route);
        return ResponseEntity.ok().build();
    }

    private Route convertToRoute(BuildRouteRequestDto dto) {
        return new Route(dto.nodes(), dto.connections());
    }
}
