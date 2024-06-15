package com.example.authentication_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${cors_allowed_origin:*}")
    private String allowedOrigin;

    @Override
    public void addCorsMappings(CorsRegistry registry) {

        registry.addMapping("/auth/register")
                .allowedMethods("POST")
                .allowedOrigins(allowedOrigin);
        registry.addMapping("/auth/login")
                .allowedMethods("POST")
                .allowedOrigins(allowedOrigin);
    }
}
