package com.wladischlau.vlt.adapters;

import com.wladischlau.vlt.adapters.common.AbstractAdapter;
import com.wladischlau.vlt.adapters.common.AdapterType;
import com.wladischlau.vlt.adapters.common.OutboundAdapter;
import com.wladischlau.vlt.adapters.config.LoggerAdapterConfig;
import lombok.Getter;
import org.slf4j.LoggerFactory;
import org.slf4j.spi.LoggingEventBuilder;
import org.springframework.integration.dsl.IntegrationFlowBuilder;

import java.util.Objects;

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
}