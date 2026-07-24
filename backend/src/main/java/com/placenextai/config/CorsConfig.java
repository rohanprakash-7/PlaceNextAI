package com.placenextai.config;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
public class CorsConfig {

    private static final Logger log = LoggerFactory.getLogger(CorsConfig.class);
    private static final String LOCALHOST_DEFAULT = "http://localhost:5173";

    @Value("${app.cors.allowed-origins}")
    private String allowedOrigins;

    private final Environment environment;

    public CorsConfig(Environment environment) {
        this.environment = environment;
    }

    // Misconfigured CORS is otherwise invisible: the browser just reports a
    // generic "no response" network error with no indication that the
    // backend itself refused the origin. Logging the resolved value at
    // startup (and screaming if it's still the local-dev default while
    // running the "prod" profile) makes this diagnosable in Render's logs
    // instead of requiring a manual curl/OPTIONS investigation every time.
    @PostConstruct
    public void logResolvedOrigins() {
        boolean isProd = Arrays.asList(environment.getActiveProfiles()).contains("prod");
        log.info("CORS allowed origins resolved to: {}", allowedOrigins);
        if (isProd && LOCALHOST_DEFAULT.equals(allowedOrigins.trim())) {
            log.warn(
                    "CORS_ALLOWED_ORIGINS is not set and the 'prod' profile is active - " +
                            "falling back to the local-dev default ({}). Every request from the " +
                            "real deployed frontend will be silently rejected by CORS until " +
                            "CORS_ALLOWED_ORIGINS is set to the frontend's actual origin.",
                    LOCALHOST_DEFAULT);
        }
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList(allowedOrigins.split(",")));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept"));
        configuration.setExposedHeaders(List.of("Authorization"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
