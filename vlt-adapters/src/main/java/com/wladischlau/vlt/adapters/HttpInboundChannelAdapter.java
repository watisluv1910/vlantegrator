package com.wladischlau.vlt.adapters;

import com.wladischlau.vlt.adapters.common.AbstractAdapter;
import com.wladischlau.vlt.adapters.common.AdapterType;
import com.wladischlau.vlt.adapters.common.InboundAdapter;
import com.wladischlau.vlt.adapters.config.HttpInboundAdapterConfig;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlowBuilder;
import org.springframework.integration.http.dsl.Http;

@Slf4j
@Getter
public class HttpInboundChannelAdapter extends AbstractAdapter<HttpInboundAdapterConfig> implements InboundAdapter {

    public HttpInboundChannelAdapter(String configJson) {
        super(configJson, HttpInboundAdapterConfig.class);
    }

    @SneakyThrows
    @Override
    public IntegrationFlowBuilder start() {
        // Полное имя класса
        var payloadType = Class.forName(config.requestPayloadType());
        log.info("Создание HTTP inbound endpoint с path={}", config.path());
        return IntegrationFlow.from(Http.inboundChannelAdapter(config.path())
                                            .requestMapping(r -> r.methods(
                                                    config.supportedMethods().toArray(HttpMethod[]::new))
                                            )
                                            .requestPayloadType(payloadType)
                                            .mergeWithDefaultConverters(true)
                                            .autoStartup(true)
        );
    }

    @Override
    public AdapterType getType() {
        return AdapterType.HTTP_INBOUND;
    }
}