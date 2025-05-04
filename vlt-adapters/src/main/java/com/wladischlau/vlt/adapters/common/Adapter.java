package com.wladischlau.vlt.adapters.common;

public sealed interface Adapter permits AbstractAdapter, InboundAdapter, OutboundAdapter {

    AdapterType getType();
}
