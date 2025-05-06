package com.wladischlau.vlt.core.integrator.model;

public record ConnectionStyle(
        String type,
        String startMarkerType,
        String endMarkerType,
        boolean animated,
        boolean focusable
) {
}
