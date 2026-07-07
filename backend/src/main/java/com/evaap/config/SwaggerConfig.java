package com.evaap.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    private static final String BEARER_SCHEME = "bearerAuth";

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("EVAAP API")
                        .description("Backend API for the EVAAP platform — Phase 1: Foundation & Authentication")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("EVAAP Backend Team")))
                // Registers the JWT bearer scheme globally — adds the "Authorize" button
                // to Swagger UI so you can paste a token once and test all protected endpoints
                // without manually adding the Authorization header to every request.
                .addSecurityItem(new SecurityRequirement().addList(BEARER_SCHEME))
                .components(new Components()
                        .addSecuritySchemes(BEARER_SCHEME, new SecurityScheme()
                                .name(BEARER_SCHEME)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Paste your access token here (without the 'Bearer' prefix — Swagger adds it automatically)")));
    }
}
