package com.wladischlau.vlt.adapters.config;

import com.wladischlau.vlt.adapters.common.AdapterConfig;
import jakarta.validation.constraints.NotBlank;

public record JdbcOutboundAdapterConfig(
        @NotBlank String query,
        @NotBlank String jdbcUrl,
        @NotBlank String jdbcUsername,
        @NotBlank String jdbcPassword
) implements AdapterConfig {}