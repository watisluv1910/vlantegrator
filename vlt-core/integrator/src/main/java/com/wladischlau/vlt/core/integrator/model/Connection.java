package com.wladischlau.vlt.core.integrator.model;

import java.util.UUID;

public record Connection(UUID id, UUID fromNodeId, UUID toNodeId) {
}
