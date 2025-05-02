package com.wladischlau.vlt.adapters;

import com.wladischlau.vlt.adapters.common.AbstractAdapter;
import com.wladischlau.vlt.adapters.common.AdapterConfig;
import com.wladischlau.vlt.adapters.common.AdapterType;
import com.wladischlau.vlt.adapters.common.OutboundAdapter;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.integration.dsl.IntegrationFlowBuilder;

import java.util.List;
import java.util.concurrent.Executors;

import static com.wladischlau.vlt.adapters.DividerAdapter.DividerAdapterConfig;

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

    public record DividerAdapterConfig(@Size(min = 2) List<String> subFlowChannels) implements AdapterConfig {}
}