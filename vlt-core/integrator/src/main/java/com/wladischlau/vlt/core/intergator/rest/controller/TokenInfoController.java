package com.wladischlau.vlt.core.intergator.rest.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api")
public class TokenInfoController {

    @Operation(
            summary = "Получить информацию о JWT токене",
            description = "Парсит JWT из текущей аутентифицированной сессии и возвращает его claims в формате JSON.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Успешное получение claims JWT",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = Map.class))
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Не авторизован – не найден действительный токен"
                    )
            }
    )
    @GetMapping("/v1/token-info")
    public ResponseEntity<Map<String, ?>> getTokenInfo(Authentication authentication) {
        // Если аутентификация представлена объектом JwtAuthenticationToken,
        // возвращаем claims из JWT токена
        if (authentication instanceof JwtAuthenticationToken jwtAuthToken) {
            var claims = jwtAuthToken.getToken().getClaims();
            log.info("JWT token: {}", jwtAuthToken.getToken().getClaims().toString());
            return ResponseEntity.ok(claims);
        }

        // Если токен отсутствует или недействителен, возвращаем сообщение об ошибке
        return ResponseEntity.badRequest()
                .body(Map.of("error", "Действительный JWT не найден в аутентификации", "isPending", true));
    }
}
