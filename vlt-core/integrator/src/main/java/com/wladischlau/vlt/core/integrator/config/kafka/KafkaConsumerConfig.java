package com.wladischlau.vlt.core.integrator.config.kafka;

import com.wladischlau.vlt.core.commons.dto.DeployStatusDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.listener.DefaultErrorHandler;

@Configuration
public class KafkaConsumerConfig {

    @Value("${spring.kafka.listener.concurrency:1}")
    private int concurrency;

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, DeployStatusDto> deployStatusConsumerFactory(
            ConsumerFactory<String, DeployStatusDto> cf,
            DefaultErrorHandler errorHandler) {
        var factory = new ConcurrentKafkaListenerContainerFactory<String, DeployStatusDto>();
        factory.setConsumerFactory(cf);
        factory.setConcurrency(concurrency);
        factory.setCommonErrorHandler(errorHandler);
        return factory;
    }
}