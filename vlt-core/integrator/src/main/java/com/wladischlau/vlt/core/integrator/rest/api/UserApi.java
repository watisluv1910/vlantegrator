package com.wladischlau.vlt.core.integrator.rest.api;

import com.wladischlau.vlt.core.integrator.rest.dto.UserSettingsDto;
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
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Tag(name = "User API", description = "Управление данными пользователей")
@Validated
@RequestMapping("/api")
@SecuritySchemes({
        @SecurityScheme(name = "bearerAuth", type = SecuritySchemeType.HTTP, scheme = "bearer", bearerFormat = "JWT"),
})
public interface UserApi {

    String GET_USER_SETTINGS = "getUserSettings";
    String UPDATE_USER_SETTINGS = "updateUserSettings";

    @Operation(
            security = @SecurityRequirement(name = "bearerAuth"),
            operationId = GET_USER_SETTINGS,
            summary = "Получить настройки пользователя",
            description = "Получить настройки пользователя",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Настройки пользователя возвращены",
                            content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserSettingsDto.class))),
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
    @GetMapping(value = "/v1/user/settings", produces = MediaType.APPLICATION_JSON_VALUE)
    default ResponseEntity<UserSettingsDto> getUserSettings(JwtAuthenticationToken principal) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }

    @Operation(
            security = @SecurityRequirement(name = "bearerAuth"),
            operationId = UPDATE_USER_SETTINGS,
            summary = "Обновить настройки пользователя",
            description = "Обновить настройки пользователя",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Настройки пользователя обновлены"),
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
    @PostMapping(value = "/v1/user/settings")
    default ResponseEntity<Void> updateUserSettings(
            @RequestBody(required = true, content = @Content(schema = @Schema(implementation = UserSettingsDto.class)))
            @org.springframework.web.bind.annotation.RequestBody UserSettingsDto request,
            JwtAuthenticationToken principal) {
        return new ResponseEntity<>(HttpStatus.NOT_IMPLEMENTED);
    }
}
