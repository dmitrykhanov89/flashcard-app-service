package com.sekhanov.flashcard.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Конфигурация веб-сервера для настройки CORS (Cross-Origin Resource Sharing).
 * <p>
 * Позволяет разрешить запросы с фронтенда, работающего на http://localhost:5173,
 * для всех путей API.
 * </p>
 */
@Configuration
public class WebConfig {

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
                        .allowedOrigins("http://localhost:5173")
                        .allowedMethods("*")
                        .allowedHeaders("*")
                        .exposedHeaders("Authorization");
            }
        };
    }
}
