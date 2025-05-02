package com.wladischlau.vlt.core.intergator.model;

import java.util.UUID;

public record Connection(UUID fromNodeId, UUID toNodeId) {
}
