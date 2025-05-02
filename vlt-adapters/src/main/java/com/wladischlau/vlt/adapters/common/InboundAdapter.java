package com.wladischlau.vlt.adapters.common;

import org.springframework.integration.dsl.IntegrationFlowBuilder;

public non-sealed interface InboundAdapter extends Adapter {

    IntegrationFlowBuilder start();
}
