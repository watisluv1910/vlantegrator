package com.wladischlau.vlt.adapters;

import com.wladischlau.vlt.adapters.common.AbstractAdapter;
import com.wladischlau.vlt.adapters.common.AdapterType;
import com.wladischlau.vlt.adapters.common.OutboundAdapter;
import com.wladischlau.vlt.adapters.config.HttpOutboundGatewayAdapterConfig;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.integration.dsl.IntegrationFlowBuilder;
import org.springframework.integration.http.dsl.Http;
import org.springframework.util.StringUtils;

@Slf4j
@Getter
public class HttpOutboundGatewayAdapter extends AbstractAdapter<HttpOutboundGatewayAdapterConfig> implements OutboundAdapter {

    public HttpOutboundGatewayAdapter(String configJson) {
        super(configJson, HttpOutboundGatewayAdapterConfig.class);
    }

    @Override
    public IntegrationFlowBuilder apply(IntegrationFlowBuilder flow) {
        Class<?> responseType = String.class;
        if (StringUtils.hasText(config.expectedResponseType())) {
            try {
                responseType = Class.forName(config.expectedResponseType());
            } catch (ClassNotFoundException e) {
                log.warn("Не удалось загрузить класс expectedResponseType: {}. Используется java.lang.String.",
                         config.expectedResponseType());
            }
        }

        log.info("Создание HTTP outbound endpoint. URL={}, httpMethod={}, expectedResponseType={}",
                 config.url(), config.httpMethod(), responseType.getName());

        return flow.handle(Http.outboundChannelAdapter(config.url())
                                   .httpMethod(config.httpMethod())
                                   .expectedResponseType(responseType));
    }

    @Override
    public AdapterType getType() {
        return AdapterType.HTTP_OUTBOUND;
    }
}
