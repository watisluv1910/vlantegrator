package com.wladischlau.vlt.adapters.config;

import com.wladischlau.vlt.adapters.common.AdapterConfig;
import com.wladischlau.vlt.adapters.utils.Slf4jLogLevelDeserializer;
import com.wladischlau.vlt.adapters.utils.SpringExpressionDeserializer;
import jakarta.validation.constraints.NotBlank;
import org.slf4j.event.Level;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.springframework.expression.Expression;

import java.util.List;

public record LoggerAdapterConfig(
        @JsonDeserialize(using = Slf4jLogLevelDeserializer.class) Level logLevel,
        @NotBlank String msgTemplate,
        @JsonDeserialize(contentUsing = SpringExpressionDeserializer.class) List<Expression> extractors
) implements AdapterConfig {}