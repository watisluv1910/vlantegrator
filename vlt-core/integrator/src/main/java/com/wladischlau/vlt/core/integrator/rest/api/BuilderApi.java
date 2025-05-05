package com.wladischlau.vlt.core.integrator.rest.api;

import com.wladischlau.vlt.core.integrator.rest.dto.BuildRouteRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.security.SecuritySchemes;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Validated
@RequestMapping("/api")
@SecuritySchemes({
        @SecurityScheme(name = "bearerAuth", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT"),
})
public interface BuilderApi {

    String BUILD_ROUTE = "buildRoute";

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
