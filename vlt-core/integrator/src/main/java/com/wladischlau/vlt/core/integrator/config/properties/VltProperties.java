package com.wladischlau.vlt.core.integrator.config.properties;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "vlt")
public class VltProperties {

    /**
     * Длина хэша версии маршрута.
     * <p>
     * Минимум 4 (16-бит), максимум 32 (128-бит).
     * </p>
     */
    @Min(4)
    @Max(32)
    private int versionHashLength;
}