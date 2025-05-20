package com.wladischlau.vlt.adapters.config;

import com.wladischlau.vlt.adapters.common.AdapterConfig;
import com.wladischlau.vlt.adapters.utils.Slf4jLogLevelDeserializer;
import com.wladischlau.vlt.adapters.utils.SpringExpressionDeserializer;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import org.slf4j.event.Level;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.springframework.expression.Expression;

import java.util.List;

public record LoggerAdapterConfig(
        @JsonDeserialize(using = Slf4jLogLevelDeserializer.class) Level logLevel,
        @NotBlank String msgTemplate,
        @JsonDeserialize(contentUsing = SpringExpressionDeserializer.class)
        @ArraySchema(schema = @Schema(implementation = String.class, example = "\"$payload\""))
        List<Expression> extractors
) implements AdapterConfig {}