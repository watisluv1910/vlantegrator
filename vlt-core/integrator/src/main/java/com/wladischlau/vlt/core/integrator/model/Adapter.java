package com.wladischlau.vlt.core.integrator.model;

import com.wladischlau.vlt.adapters.common.AdapterType;

import java.util.UUID;

public record Adapter(
        UUID id,
        String name,
        String displayName,
        String description,
        String clazz,
        String type,
        AdapterType.Direction direction,
        AdapterType.ChannelKind channelKind
) {
}
