package com.wladischlau.vlt.adapters;

import com.wladischlau.vlt.adapters.common.AbstractAdapter;
import com.wladischlau.vlt.adapters.common.AdapterType;
import com.wladischlau.vlt.adapters.common.OutboundAdapter;
import com.wladischlau.vlt.adapters.config.DividerAdapterConfig;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.integration.dsl.IntegrationFlowBuilder;

import java.util.concurrent.Executors;

@Slf4j
@Getter
public class DividerAdapter extends AbstractAdapter<DividerAdapterConfig> implements OutboundAdapter {

    public DividerAdapter(String configJson) {
        super(configJson, DividerAdapterConfig.class);
    }

    @Override
    @SuppressWarnings("CodeBlock2Expr")
    public IntegrationFlowBuilder apply(IntegrationFlowBuilder flow) {
        log.info("Инициализация разделителя flow на каналы: {}", config.subFlowChannels());

        return flow.publishSubscribeChannel(Executors.newCachedThreadPool(), ps -> {
            config.subFlowChannels().forEach(channel -> {
                ps.subscribe(subFlow -> {
                    subFlow.channel(channel);
                    log.info("Flow направлен в канал: {}", channel);
                });
            });
        });
    }

    @Override
    public AdapterType getType() {
        return AdapterType.DIVIDER;
    }
}