package com.wladischlau.vlt.core.intergator.model;

import java.util.Map;
import java.util.UUID;

public record Node(UUID id, String adapterName, String adapterClassName, Map<String, Object> adapterConfig) {
}
