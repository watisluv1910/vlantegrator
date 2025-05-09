package com.wladischlau.vlt.core.integrator.model;

import java.util.Map;

public record NodeStyle(
        String role,
        Map<String, Object> style
) {
}
