package com.wladischlau.vlt.core.integrator.rest.api;

import com.wladischlau.vlt.core.commons.dto.RouteIdDto;
import com.wladischlau.vlt.core.integrator.rest.dto.CreateRouteRequestDto;
import com.wladischlau.vlt.core.integrator.rest.dto.RouteDefinitionDto;
import com.wladischlau.vlt.core.integrator.rest.dto.RouteDto;
import com.wladischlau.vlt.core.integrator.rest.dto.RouteUserActionDto;
import com.wladischlau.vlt.core.integrator.rest.dto.UpdateRouteRequestDto;
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
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;

@Tag(name = "Routes API", description = "Операции для работы с маршрутами")
@Validated
@RequestMapping("/api")
@SecuritySchemes({
        @SecurityScheme(name = "bearerAuth", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT"),
})
public interface RouteApi {

    String CREATE_ROUTE = "createRoute";
    String GET_ALL_ROUTES = "getAllRoutes";
    String GET_ROUTE = "getRoute";
    String UPDATE_ROUTE = "updateRoute";
    String GET_ROUTE_VERSIONS = "getRouteVersions";
    String GET_ROUTE_DEFINITION = "getRouteDefinition";
    String UPDATE_ROUTE_DEFINITION = "updateRouteDefinition";
    String DELETE_ROUTE = "deleteRoute";
    String BUILD_ROUTE = "buildRoute";
    String DEPLOY_ROUTE = "deployRoute";
    String GET_ROUTE_USER_ACTIONS = "getRouteUserActions";

    @Operation(
            security = @SecurityRequirement(name = "bearerAuth"),
            operationId = CREATE_ROUTE,
            summary = "Создать маршрут",
            description = "Создаёт новый маршрут на основе переданной конфигурации",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Маршрут создан",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = RouteIdDto.class))),
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
    @PostMapping(value = "/v1/route", produces = {MediaType.APPLICATION_JSON_VALUE})
    default ResponseEntity<RouteIdDto> createRoute(
            @RequestBody(required = true, content = @Content(schema = @Schema(implementation = CreateRouteRequestDto.class)))
            @org.springframework.web.bind.annotation.RequestBody CreateRouteRequestDto request,
            JwtAuthenticationToken principal) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @Operation(
            security = @SecurityRequirement(name = "bearerAuth"),
            operationId = GET_ALL_ROUTES,
            summary = "Получить информацию о всех маршрутах",
            description = "Отдаёт информацию о всех маршрутах",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Информация о всех маршрутах",
                            content = @Content(mediaType = "application/json")),
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
    @GetMapping(value = "/v1/route", produces = {MediaType.APPLICATION_JSON_VALUE})
    default ResponseEntity<List<RouteDto>> getAllRoutes(JwtAuthenticationToken principal) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @Operation(
            security = @SecurityRequirement(name = "bearerAuth"),
            operationId = GET_ROUTE,
            summary = "Получить информацию о маршруте",
            description = "Отдаёт информацию о маршруте",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Информация о маршруте",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = RouteDto.class))),
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
    @GetMapping(value = "/v1/route/{id}", produces = {MediaType.APPLICATION_JSON_VALUE})
    default ResponseEntity<RouteDto> getRoute(
            @Parameter(required = true, schema = @Schema(description = "ID маршрута", type = "string", format = "uuid"))
            @NotNull @PathVariable(name = "id") UUID id,
            JwtAuthenticationToken principal) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @Operation(
            security = @SecurityRequirement(name = "bearerAuth"),
            operationId = GET_ROUTE_VERSIONS,
            summary = "Получить информацию об имеющихся в кэше версиях маршрута",
            description = "Получить версии маршрута, структура которых хранится в кэше",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Список версий",
                            content = @Content(mediaType = "application/json")),
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
    @GetMapping(value = "/v1/route/{id}/versions", produces = {MediaType.APPLICATION_JSON_VALUE})
    default ResponseEntity<List<String>> getRouteCachedVersions(
            @Parameter(required = true, schema = @Schema(description = "ID маршрута", type = "string", format = "uuid"))
            @NotNull @PathVariable(name = "id") UUID id,
            JwtAuthenticationToken principal) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @Operation(
            security = @SecurityRequirement(name = "bearerAuth"),
            operationId = GET_ROUTE_USER_ACTIONS,
            summary = "Получить действия пользователей над маршрутами",
            description = "Получить список действий, предпринимаемых текущим или всеми пользователями над маршрутами " +
                    "в платформе, отсортированный по времени совершения действия от нового к старому",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Список действий нам маршрутами",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = RouteIdDto.class))),
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
    @GetMapping(value = "/v1/route/actions", produces = {MediaType.APPLICATION_JSON_VALUE})
    default ResponseEntity<List<RouteUserActionDto>> getRouteUserActions(
            @Parameter(required = true, schema = @Schema(description = "Возвращать только действия текущего пользователя", type = "boolean"))
            @NotNull @RequestParam(name = "personal") boolean displayPersonal,
            JwtAuthenticationToken principal) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @Operation(
            security = @SecurityRequirement(name = "bearerAuth"),
            operationId = GET_ROUTE_DEFINITION,
            summary = "Получить структуру маршрута",
            description = "Отдаёт структуру маршрута в качестве ответа",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Структура маршрута получена",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = RouteDefinitionDto.class))),
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
    @GetMapping(value = "/v1/route/{id}/{versionHash}/definition", produces = {MediaType.APPLICATION_JSON_VALUE})
    default ResponseEntity<RouteDefinitionDto> getRouteDefinition(
            @Parameter(required = true, schema = @Schema(description = "ID маршрута", type = "string", format = "uuid"))
            @NotNull @PathVariable(name = "id") UUID id,
            @Parameter(required = true, schema = @Schema(description = "Хэш-код версии маршрута", type = "string"))
            @NotNull @PathVariable(name = "versionHash") String versionHash,
            JwtAuthenticationToken principal) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @Operation(
            security = @SecurityRequirement(name = "bearerAuth"),
            operationId = UPDATE_ROUTE,
            summary = "Обновить конфиг маршрута",
            description = "Обновляет конфиг имеющегося маршрута на основе переданной конфигурации",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Конфиг маршрута обновлён"),
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
    @PatchMapping(value = "/v1/route/{id}")
    default ResponseEntity<Void> updateRoute(
            @Parameter(required = true, schema = @Schema(description = "ID маршрута", type = "string", format = "uuid"))
            @NotNull @PathVariable(name = "id") UUID id,
            @RequestBody(required = true, content = @Content(schema = @Schema(implementation = UpdateRouteRequestDto.class)))
            @org.springframework.web.bind.annotation.RequestBody UpdateRouteRequestDto request,
            JwtAuthenticationToken principal) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @Operation(
            security = @SecurityRequirement(name = "bearerAuth"),
            operationId = UPDATE_ROUTE_DEFINITION,
            summary = "Обновить структуру маршрута",
            description = "Обновляет структуру имеющегося маршрута на основе переданной конфигурации",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Структура маршрута обновлена",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = RouteIdDto.class))),
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
    @PostMapping(value = "/v1/route/{id}/{versionHash}/definition", produces = {MediaType.APPLICATION_JSON_VALUE})
    default ResponseEntity<RouteIdDto> updateRouteDefinition(
            @Parameter(required = true, schema = @Schema(description = "ID маршрута", type = "string", format = "uuid"))
            @NotNull @PathVariable(name = "id") UUID id,
            @Parameter(required = true, schema = @Schema(description = "Хэш-код версии маршрута", type = "string"))
            @NotNull @PathVariable(name = "versionHash") String versionHash,
            @RequestBody(required = true, content = @Content(schema = @Schema(implementation = RouteDefinitionDto.class)))
            @org.springframework.web.bind.annotation.RequestBody RouteDefinitionDto request,
            JwtAuthenticationToken principal) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @Operation(
            security = @SecurityRequirement(name = "bearerAuth"),
            operationId = DELETE_ROUTE,
            summary = "Удалить маршрут",
            description = "Полностью удалить маршрут, в том числе все его контейнер и образы",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Маршрут удалён"),
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
    @DeleteMapping(value = "/v1/route/{id}")
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
            description = "Запускает процесс сборки маршрута (compile + build image) в фоновом режиме",
            responses = {
                    @ApiResponse(responseCode = "202", description = "Запрос на сборку принят"),
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
    @PostMapping(value = "/v1/route/{id}/build")
    default ResponseEntity<Void> buildRoute(
            @Parameter(required = true, schema = @Schema(description = "ID маршрута", type = "string", format = "uuid"))
            @NotNull @PathVariable(name = "id") UUID id,
            JwtAuthenticationToken principal) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @Operation(
            security = @SecurityRequirement(name = "bearerAuth"),
            operationId = DEPLOY_ROUTE,
            summary = "Развернуть маршрут",
            description = "Выполнить действие по отношению к развёртыванию маршрута: запустить, остановить, удалить, перезапустить",
            responses = {
                    @ApiResponse(responseCode = "202", description = "Запрос на выполнение действия по отношению к развёртыванию маршрута принят"),
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
    @PostMapping(value = "/v1/route/{id}/deploy")
    default ResponseEntity<Void> deployRoute(
            @Parameter(required = true, schema = @Schema(description = "ID маршрута", type = "string", format = "uuid"))
            @NotNull @PathVariable(name = "id") UUID id,
            @Parameter(required = true, schema = @Schema(description = "Действие", type = "string"))
            @NotNull @RequestParam(name = "action") String action,
            JwtAuthenticationToken principal) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }
}
