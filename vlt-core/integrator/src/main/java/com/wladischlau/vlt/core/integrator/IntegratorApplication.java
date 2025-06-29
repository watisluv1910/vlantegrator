package com.wladischlau.vlt.core.integrator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication(
        scanBasePackages = {
                "com.wladischlau.vlt.core.integrator",
                "com.wladischlau.vlt.core.jooq"
        }
)
@ConfigurationPropertiesScan
@EnableAsync
@EnableKafka
public class IntegratorApplication {

    public static void main(String[] args) {
        SpringApplication.run(IntegratorApplication.class, args);
    }
}
