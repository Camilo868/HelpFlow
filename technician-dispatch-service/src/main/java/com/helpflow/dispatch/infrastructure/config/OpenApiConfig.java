package com.helpflow.dispatch.infrastructure.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "HelpFlow - Technician Dispatch Simulator API",
                version = "1.0.0",
                description = "External Simulated Technician Dispatch Service with Deterministic Availability and SLA Classification",
                contact = @Contact(name = "HelpFlow Dispatch Team", email = "dispatch@helpflow.com")
        )
)
public class OpenApiConfig {
}
