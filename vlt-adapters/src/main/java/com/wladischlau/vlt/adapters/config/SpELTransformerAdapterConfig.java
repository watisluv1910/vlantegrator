package com.wladischlau.vlt.adapters.config;

import com.wladischlau.vlt.adapters.common.AdapterConfig;
import jakarta.validation.constraints.NotBlank;

public record SpELTransformerAdapterConfig(@NotBlank String expression) implements AdapterConfig {}
