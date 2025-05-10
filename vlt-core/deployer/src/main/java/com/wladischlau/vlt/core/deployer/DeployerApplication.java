package com.wladischlau.vlt.core.deployer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@ConfigurationPropertiesScan
@EnableKafka
public class DeployerApplication {

    public static void main(String[] args) {
        SpringApplication.run(DeployerApplication.class, args);
    }
}
