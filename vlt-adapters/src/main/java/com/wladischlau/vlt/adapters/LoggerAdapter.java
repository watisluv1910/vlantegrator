package com.wladischlau.vlt.adapters;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.wladischlau.vlt.adapters.common.AbstractAdapter;
import com.wladischlau.vlt.adapters.common.AdapterConfig;
import com.wladischlau.vlt.adapters.common.AdapterType;
import com.wladischlau.vlt.adapters.common.OutboundAdapter;
import com.wladischlau.vlt.adapters.utils.Slf4jLogLevelDeserializer;
import com.wladischlau.vlt.adapters.utils.SpringExpressionDeserializer;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import org.slf4j.spi.LoggingEventBuilder;
import org.springframework.expression.Expression;
import org.springframework.integration.dsl.IntegrationFlowBuilder;

import java.util.List;
import java.util.Objects;

import static com.wladischlau.vlt.adapters.LoggerAdapter.LoggerAdapterConfig;

@Getter
public class LoggerAdapter extends AbstractAdapter<LoggerAdapterConfig> implements OutboundAdapter {

    private final LoggingEventBuilder eventLogger;

    public LoggerAdapter(String configJson) {
        super(configJson, LoggerAdapterConfig.class);
        this.eventLogger = LoggerFactory.getLogger(LoggerAdapter.class).atLevel(config.logLevel());
    }

    @Override
    public IntegrationFlowBuilder apply(IntegrationFlowBuilder flow) {
        return flow.handle(msg -> {
            var arguments = config.extractors().stream()
                    .map(ext -> ext.getValue(msg))
                    .filter(Objects::nonNull)
                    .toArray(Object[]::new);

            eventLogger.log(config.msgTemplate(), arguments);
        });
    }

    @Override
    public AdapterType getType() {
        return AdapterType.LOGGER;
    }

    public record LoggerAdapterConfig(
            @JsonDeserialize(using = Slf4jLogLevelDeserializer.class) Level logLevel,
            @NotBlank String msgTemplate,
            @JsonDeserialize(contentUsing = SpringExpressionDeserializer.class) List<Expression> extractors
    ) implements AdapterConfig {}
}