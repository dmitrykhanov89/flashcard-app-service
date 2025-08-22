package com.sekhanov.flashcard.service;

import java.util.Date;
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
    /**
     * Обновляет (рефрешит) JWT токен, создавая новый на основе старого.
     * @param token текущий JWT токен
     * @return новый JWT токен с обновлённым временем жизни
     */
    String refreshToken(String token);
    /**
     * Извлекает дату истечения срока действия токена.
     * @param token JWT токен
     * @return дата истечения срока действия токена
     */
    Date extractExpiration(String token);
}
