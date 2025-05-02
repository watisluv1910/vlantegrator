package com.wladischlau.vlt.core.intergator.model;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
@Builder
public final class FlowDefinition {
    private final String channel;
    private final Node parent;
    private final List<Node> nodes;
    private final Set<String> subflowChannels;

    public boolean isSubFlow() {
        return parent != null;
    }
}