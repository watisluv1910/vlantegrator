package com.wladischlau.vlt.core.deployer.config.kafka;

import org.apache.kafka.common.errors.SerializationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
public class KafkaConfig {

    @Value("${spring.kafka.listener.concurrency:1}")
    private int concurrency;

    @Bean
    public TaskScheduler taskScheduler() {
        var scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(concurrency);
        scheduler.setThreadNamePrefix("kafka-retry-");
        scheduler.initialize();
        return scheduler;
    }

    @Bean
    public DefaultErrorHandler errorHandler() {
        var handler = new DefaultErrorHandler(new FixedBackOff(1000L, 3));
        handler.addNotRetryableExceptions(SerializationException.class, IllegalStateException.class);
        return handler;
    }
}
