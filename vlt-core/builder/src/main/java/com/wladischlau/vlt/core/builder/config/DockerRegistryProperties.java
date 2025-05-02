package com.wladischlau.vlt.core.builder.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "docker")
public record DockerRegistryProperties(String username, String password, String imageRegistry) {
}
