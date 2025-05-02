package com.wladischlau.vlt.core.builder.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "repo")
public record MavenRepoProperties(String id, String name, String url) {
}
