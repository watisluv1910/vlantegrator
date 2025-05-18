package com.wladischlau.vlt.core.integrator.model;

public record UserSettings(EditorSettings editorSettings, AccessibilitySettings accessibilitySettings) {

    public record EditorSettings(boolean showGrid, String defaultViewportPosition, long autosaveIntervalMs) {}

    public record AccessibilitySettings(boolean disableAnimations, boolean enableHighContrast) {}
}
