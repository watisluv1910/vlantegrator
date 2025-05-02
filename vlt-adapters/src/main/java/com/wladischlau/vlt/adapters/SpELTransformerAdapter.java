package com.wladischlau.vlt.adapters;

import com.wladischlau.vlt.adapters.common.AbstractAdapter;
import com.wladischlau.vlt.adapters.common.AdapterType;
import com.wladischlau.vlt.adapters.common.OutboundAdapter;
import com.wladischlau.vlt.adapters.config.SpELTransformerAdapterConfig;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.integration.dsl.IntegrationFlowBuilder;

@Slf4j
@Getter
public class SpELTransformerAdapter extends AbstractAdapter<SpELTransformerAdapterConfig> implements OutboundAdapter {

    public SpELTransformerAdapter(String configJson) {
        super(configJson, SpELTransformerAdapterConfig.class);
    }

    @Override
    public IntegrationFlowBuilder apply(IntegrationFlowBuilder flow) {
        var parser = new SpelExpressionParser();
        var expression = parser.parseExpression(config.expression());
        log.info("Используется выражение трансформации: {}", expression.getExpressionString());
        return flow.transform(expression::getValue);
    }

    @Override
    public AdapterType getType() {
        return AdapterType.SPEL_TRANSFORMER;
    }
}