package com.sekhanov.flashcard.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO для запроса аутентификации пользователя.
 * <p>
 * Содержит данные для входа — логин и пароль.
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthRequest {
    private String login;
    private String password;

}
