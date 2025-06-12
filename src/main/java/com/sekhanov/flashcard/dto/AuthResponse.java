package com.sekhanov.flashcard.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO для ответа аутентификации пользователя.
 * <p>
 * Содержит JWT токен при успешной аутентификации
 * или сообщение об ошибке при неудаче.
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String errorMessage;
}

