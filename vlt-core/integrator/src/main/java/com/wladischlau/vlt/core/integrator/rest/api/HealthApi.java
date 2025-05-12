package com.wladischlau.vlt.core.integrator.rest.api;

import com.wladischlau.vlt.core.integrator.rest.dto.PlatformBasicHealthDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.security.SecuritySchemes;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "Health API", description = "Информация о состоянии системы")
@Validated
@RequestMapping("/api")
@SecuritySchemes({
        @SecurityScheme(name = "bearerAuth", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT"),
})
public interface HealthApi {

    String GET_BASIC_HEALTH = "getBasicHealth";

    @Operation(
            security = @SecurityRequirement(name = "bearerAuth"),
            operationId = GET_BASIC_HEALTH,
            summary = "Получить основную информацию о состоянии системы",
            description = "Получить информацию о потребляемых ресурсах процессора и памяти, а также о здоровье БД ядра и кластеров Kafka",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Основная информация о состоянии системы",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = PlatformBasicHealthDto.class))),
                    @ApiResponse(responseCode = "400", description = "Некорректный запрос",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class))),
                    @ApiResponse(responseCode = "401", description = "Не авторизован",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class))),
                    @ApiResponse(responseCode = "403", description = "Доступ запрещён",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class))),
                    @ApiResponse(responseCode = "500", description = "Ошибка сервера",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = ProblemDetail.class))),
            }
    )
    @GetMapping(value = "/v1/health/basic", produces = {MediaType.APPLICATION_JSON_VALUE})
    default ResponseEntity<PlatformBasicHealthDto> getBasicHealth(JwtAuthenticationToken principal) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }
}
