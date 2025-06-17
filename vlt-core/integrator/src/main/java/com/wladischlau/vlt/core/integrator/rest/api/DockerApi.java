package com.wladischlau.vlt.core.integrator.rest.api;

import com.wladischlau.vlt.core.integrator.rest.dto.DockerNetworkDto;
import com.wladischlau.vlt.core.integrator.rest.dto.RouteDto;
import com.wladischlau.vlt.core.integrator.rest.dto.SearchRoutesRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Tag(name = "Docker API", description = "Прямое взаимодействие с Docker")
@Validated
@RequestMapping("/api")
@SecuritySchemes({
        @SecurityScheme(name = "bearerAuth", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT"),
})
public interface DockerApi {

    String CREATE_DOCKER_NETWORK = "createDockerNetwork";
    String GET_DOCKER_NETWORKS = "getDockerNetworks";

    @Operation(
            security = @SecurityRequirement(name = "bearerAuth"),
            operationId = CREATE_DOCKER_NETWORK,
            summary = "Создать docker-сеть",
            description = "Создать docker-сеть",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Создать docker-сеть"),
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
    @PostMapping(value = "/v1/docker/networks")
    default ResponseEntity<Void> createNetwork(
            @RequestBody(required = true, content = @Content(schema = @Schema(implementation = DockerNetworkDto.class)))
            @org.springframework.web.bind.annotation.RequestBody DockerNetworkDto request,
            JwtAuthenticationToken principal) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @Operation(
            security = @SecurityRequirement(name = "bearerAuth"),
            operationId = GET_DOCKER_NETWORKS,
            summary = "Получить docker-сети",
            description = "Получить список всех имеющихся docker-сетей",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Доступные docker-сети",
                            content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation =  DockerNetworkDto.class)))),
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
    @GetMapping(value = "/v1/docker/networks", produces = {MediaType.APPLICATION_JSON_VALUE})
    default ResponseEntity<List<DockerNetworkDto>> getAvailableNetworks(JwtAuthenticationToken principal) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }
}
