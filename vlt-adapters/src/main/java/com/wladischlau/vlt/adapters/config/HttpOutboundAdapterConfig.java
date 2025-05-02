package com.wladischlau.vlt.adapters.config;

import com.wladischlau.vlt.adapters.common.AdapterConfig;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpMethod;

public record HttpOutboundAdapterConfig(
        @NotBlank String url,
        @NotNull HttpMethod httpMethod,
        String expectedResponseType
) implements AdapterConfig {

    public HttpOutboundAdapterConfig(@NotBlank String url,
                                     @NotNull HttpMethod httpMethod) {
        this(url, httpMethod, null);
    }
}