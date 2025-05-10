package com.wladischlau.vlt.core.integrator.config.kafka;

import com.wladischlau.vlt.core.commons.dto.DeployRequestDto;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

@Configuration
public class KafkaProducerConfig {

    @Bean
    public KafkaTemplate<String, DeployRequestDto> deployRequestKafkaTemplate(
            ProducerFactory<String, DeployRequestDto> pf) {
        return new KafkaTemplate<>(pf);
    }
}