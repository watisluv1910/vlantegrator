package com.wladischlau.vlt.adapters.config;

import com.wladischlau.vlt.adapters.common.AdapterConfig;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.http.HttpMethod;

import java.util.List;

/**
 * Запись конфигурации для HttpInboundAdapter.
 *
 * @param path               обязательное поле.
 * @param requestPayloadType тип данных тела запроса, ex. {@code java.lang.String}.
 * @param supportedMethods   список допустимых HTTP-методов.
 */
public record HttpInboundAdapterConfig(
        @NotBlank String path,
        @NotBlank String requestPayloadType,
        @NotEmpty List<HttpMethod> supportedMethods
) implements AdapterConfig {}