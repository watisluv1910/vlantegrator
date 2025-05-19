package com.wladischlau.vlt.core.integrator.rest.api;

import com.wladischlau.vlt.core.integrator.rest.dto.AdapterDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.security.SecuritySchemes;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.UUID;

@Tag(name = "Adapters API", description = "Операции для работы с адаптерами маршрутов")
@Validated
@RequestMapping("/api")
@SecuritySchemes({
        @SecurityScheme(name = "bearerAuth", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT"),
})
public interface AdapterApi {

    String GET_ALL_ADAPTERS = "getAllAdapters";
    String GET_ADAPTER_CONFIG_SCHEMA = "getAdapterConfigSchema";

    @Operation(
            security = @SecurityRequirement(name = "bearerAuth"),
            operationId = GET_ALL_ADAPTERS,
            summary = "Получить информацию об адаптерах",
            description = "Возвращает краткое описание всех имеющихся адаптеров",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Описания получены"),
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
    @GetMapping(value = "/v1/adapter", produces = {MediaType.APPLICATION_JSON_VALUE})
    default ResponseEntity<List<AdapterDto>> getInteractableAdapters(JwtAuthenticationToken principal) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @Operation(
            security = @SecurityRequirement(name = "bearerAuth"),
            operationId = GET_ADAPTER_CONFIG_SCHEMA,
            summary = "Получить описание конфигурации адаптера",
            description = "Возвращает описание конфигурации адаптера с переданным ID в формате JSONSchema",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Конфигурация получена"),
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
    @GetMapping(value = "/v1/adapter/{id}/config", produces = {MediaType.APPLICATION_JSON_VALUE})
    default ResponseEntity<String> getAdapterConfigSchema(
            @Parameter(required = true, schema = @Schema(description = "ID адаптера", type = "string", format = "uuid"))
            @NotNull @PathVariable(name = "id") UUID id,
            JwtAuthenticationToken principal) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }
}
