package com.wladischlau.vlt.core.integrator.model;

import java.util.Map;
import java.util.UUID;

public record Node(UUID id, String name, Adapter adapter, Map<String, Object> config) {
}
