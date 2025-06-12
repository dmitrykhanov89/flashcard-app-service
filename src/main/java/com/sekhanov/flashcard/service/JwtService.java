package com.sekhanov.flashcard.service;

/**
 * Сервис для работы с JWT (JSON Web Token).
 * <p>
 * Предоставляет методы для генерации токена по логину пользователя,
 * извлечения логина из токена и проверки валидности токена.
 * </p>
 */
public interface JwtService {
    /**
     * Генерирует JWT-токен по логину.
     * @param login логин пользователя
     * @return JWT токен
     */
    String generateToken(String login);
    /**
     * Извлекает логин из токена.
     * @param token JWT токен
     * @return логин пользователя
     */
    String extractLogin(String token);
    /**
     * Проверяет валидность токена.
     * @param token JWT токен
     * @return true если валиден, иначе false
     */
    boolean validateToken(String token);
}
