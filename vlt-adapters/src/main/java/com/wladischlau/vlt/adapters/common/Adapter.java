package com.wladischlau.vlt.adapters.common;

import com.fasterxml.jackson.databind.JsonNode;

public sealed interface Adapter permits AbstractAdapter, InboundAdapter, OutboundAdapter {

    AdapterType getType();
    JsonNode getConfigSchema();
}
