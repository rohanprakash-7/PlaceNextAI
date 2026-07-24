package com.placenextai.config;

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

    private static final String SECURITY_SCHEME_NAME = "bearerAuth";

    @Bean
    public OpenAPI placeNextAiOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("PlaceNextAI Backend API")
                        .description("""
                                REST API for PlaceNextAI, an intelligent placement readiness, recruitment and \
                                career success platform. Covers authentication, resume analysis, skill-gap \
                                roadmaps, eligibility checking, AI mock interviews, job applications, alumni \
                                mentorship, gamification, admin analytics, notifications and more. \
                                All endpoints (except register/login and health) require a Bearer JWT - \
                                obtain one via /api/auth/{role}/login and use \"Authorize\" below.""")
                        .version("v1.0.0")
                        .contact(new Contact().name("PlaceNextAI Team")))
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
                .components(new Components().addSecuritySchemes(
                        SECURITY_SCHEME_NAME,
                        new SecurityScheme()
                                .name(SECURITY_SCHEME_NAME)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }
}
