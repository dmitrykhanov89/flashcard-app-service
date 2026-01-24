package com.sekhanov.flashcard.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * Конфигурация веб-сервера для настройки CORS (Cross-Origin Resource Sharing).
 * <p>
 * Позволяет разрешить запросы с фронтенда, работающего на allowedOrigins,
 * для всех путей API.
 * </p>
 */
@Configuration
public class WebConfig {

    private final List<String> allowedOrigins;

    public WebConfig(@Value("${app.cors.allowed-origins}") List<String> allowedOrigins) {
        this.allowedOrigins = allowedOrigins;
    }

    /**
     * Создает {@link WebMvcConfigurer} для настройки CORS.
     * <p>
     * Настраивает разрешенные источники запросов, методы и заголовки.
     * </p>
     *
     * @return настроенный WebMvcConfigurer с правилами CORS
     */
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry
                        .addMapping("/**")
                        .allowedOrigins(allowedOrigins.toArray(String[]::new))
                        .allowedMethods("*")
                        .allowedHeaders("*")
                        .allowCredentials(true) // Важно для CSRF cookie
                        .exposedHeaders("Authorization", "X-CSRF-TOKEN"); // Добавляем CSRF токен в exposed headers
            }
        };
    }
}