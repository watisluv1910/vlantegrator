package com.wladischlau.vlt.core.integrator.model;

import java.util.UUID;

public record Connection(UUID fromNodeId, UUID toNodeId) {
}
