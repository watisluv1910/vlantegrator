package com.wladischlau.vlt.adapters.common;

import org.springframework.integration.dsl.IntegrationFlowBuilder;

public non-sealed interface OutboundAdapter extends Adapter {

    IntegrationFlowBuilder apply(IntegrationFlowBuilder flow);
}
