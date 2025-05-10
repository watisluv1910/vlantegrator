package com.wladischlau.vlt.core.deployer.config.kafka;

import com.wladischlau.vlt.core.commons.dto.DeployRequestDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;

@Configuration
public class KafkaConsumerConfig {

    @Value("${spring.kafka.listener.concurrency:1}")
    private int concurrency;

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, DeployRequestDto> deployRequestConsumerFactory(
            ConsumerFactory<String, DeployRequestDto> consumerFactory) {
        var factory = new ConcurrentKafkaListenerContainerFactory<String, DeployRequestDto>();
        factory.setConsumerFactory(consumerFactory);
        factory.setConcurrency(concurrency);
        return factory;
    }
}