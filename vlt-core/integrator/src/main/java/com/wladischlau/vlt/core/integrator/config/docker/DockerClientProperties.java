package com.wladischlau.vlt.core.integrator.config.docker;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "docker")
public record DockerClientProperties(String host, String username, String password, String imageRegistry) {
}
