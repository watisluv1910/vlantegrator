package com.wladischlau.vlt.core.integrator.rest.api;

import com.wladischlau.vlt.core.integrator.rest.dto.BuildRouteRequestDto;
import com.wladischlau.vlt.core.integrator.rest.dto.CreateRouteRequestDto;
import com.wladischlau.vlt.core.integrator.rest.dto.RouteIdDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.security.SecuritySchemes;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.UUID;

@Validated
@RequestMapping("/api")
@SecuritySchemes({
        @SecurityScheme(name = "bearerAuth", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT"),
})
public interface RouteApi {

    String CREATE_ROUTE = "createRoute";
    String DELETE_ROUTE = "deleteRoute";
    String BUILD_ROUTE = "buildRoute";

    @Operation(
            security = @SecurityRequirement(name = "bearerAuth"),
            operationId = CREATE_ROUTE,
            summary = "Создать маршрут",
            description = "Создаёт новый маршрут на основе переданной конфигурации",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Маршрут создан"),
                    @ApiResponse(responseCode = "400", description = "Некорректный запрос",
                            content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
                    @ApiResponse(responseCode = "401", description = "Не авторизован",
                            content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
                    @ApiResponse(responseCode = "403", description = "Доступ запрещён",
                            content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
                    @ApiResponse(responseCode = "500", description = "Ошибка сервера",
                            content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            }
    )
    @Tag(name = "routes")
    @PostMapping("/v1/route")
    default ResponseEntity<RouteIdDto> createRoute(
            @RequestBody(required = true, content = @Content(schema = @Schema(implementation = CreateRouteRequestDto.class)))
            @org.springframework.web.bind.annotation.RequestBody CreateRouteRequestDto request,
            JwtAuthenticationToken principal) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @Operation(
            security = @SecurityRequirement(name = "bearerAuth"),
            operationId = DELETE_ROUTE,
            summary = "Удалить маршрут",
            description = "Полностью удалить маршрут, в том числе все его контейнер и образы",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Маршрут удалён"),
                    @ApiResponse(responseCode = "400", description = "Некорректный запрос",
                            content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
                    @ApiResponse(responseCode = "401", description = "Не авторизован",
                            content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
                    @ApiResponse(responseCode = "403", description = "Доступ запрещён",
                            content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
                    @ApiResponse(responseCode = "500", description = "Ошибка сервера",
                            content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            }
    )
    @Tag(name = "routes")
    @DeleteMapping("/v1/route/{id}")
    default ResponseEntity<Void> deleteRoute(
            @Parameter(required = true, schema = @Schema(description = "ID маршрута", type = "string", format = "uuid"))
            @NotNull @PathVariable(name = "id") UUID id,
            JwtAuthenticationToken principal) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @Operation(
            security = @SecurityRequirement(name = "bearerAuth"),
            operationId = BUILD_ROUTE,
            summary = "Асинхронная сборка маршрута",
            description = "Принимает описание маршрута в DTO и запускает процесс сборки в фоновом режиме",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Запрос на сборку принят"),
                    @ApiResponse(responseCode = "400", description = "Некорректный запрос",
                            content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
                    @ApiResponse(responseCode = "401", description = "Не авторизован",
                            content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
                    @ApiResponse(responseCode = "403", description = "Доступ запрещён",
                            content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
                    @ApiResponse(responseCode = "500", description = "Ошибка сервера",
                            content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            }
    )
    @Tag(name = "routes")
    @PostMapping("/v1/route/build")
    default ResponseEntity<Void> buildRoute(
            @RequestBody(required = true, content = @Content(schema = @Schema(implementation = BuildRouteRequestDto.class)))
            @org.springframework.web.bind.annotation.RequestBody BuildRouteRequestDto request,
            JwtAuthenticationToken principal) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }
}
