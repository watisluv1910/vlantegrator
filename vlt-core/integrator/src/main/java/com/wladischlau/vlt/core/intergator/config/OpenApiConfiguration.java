package com.wladischlau.vlt.core.intergator.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Vlantegrator API",
                version = "${api.version}",
                description = "${api.description}",
                contact = @Contact(name = "Support", email = "vlantegrator-support@gmail.com")
        )
) // TODO: Configure security later
public class OpenApiConfiguration {}