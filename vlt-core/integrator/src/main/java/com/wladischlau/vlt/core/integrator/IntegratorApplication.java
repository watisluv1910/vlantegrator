package com.wladischlau.vlt.core.integrator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class IntegratorApplication {

    public static void main(String[] args) {
        SpringApplication.run(IntegratorApplication.class, args);
    }
}
