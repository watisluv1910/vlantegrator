package com.wladischlau.vlt.core.deployer.config.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
@RequiredArgsConstructor
public class DockerClientConfig {

    public static final int DOCKER_CLIENT_MAX_CONNECTIONS = 100;
    public static final int DOCKER_CLIENT_CONNECTION_TIMEOUT_SEC = 30;
    public static final int DOCKER_CLIENT_RESPONSE_TIMEOUT_SEC = 45;

    private final DockerClientProperties props;

    @Bean
    public DockerClient dockerClient() {
        var config = DefaultDockerClientConfig.createDefaultConfigBuilder()
                .withDockerHost(props.host())
                .withRegistryUsername(props.username())
                .withRegistryPassword(props.password())
                .withRegistryUrl(props.imageRegistry())
                .build();

        var client = new ApacheDockerHttpClient.Builder()
                .dockerHost(config.getDockerHost())
                .maxConnections(DOCKER_CLIENT_MAX_CONNECTIONS)
                .connectionTimeout(Duration.ofSeconds(DOCKER_CLIENT_CONNECTION_TIMEOUT_SEC))
                .responseTimeout(Duration.ofSeconds(DOCKER_CLIENT_RESPONSE_TIMEOUT_SEC))
                .build();

        return DockerClientImpl.getInstance(config, client);
    }
}