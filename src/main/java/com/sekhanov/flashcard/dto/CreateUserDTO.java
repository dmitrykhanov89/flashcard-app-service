package com.sekhanov.flashcard.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * DTO для создания нового пользователя.
 * <p>
 * Содержит основные данные, необходимые для регистрации пользователя:
 * имя, фамилию, логин и пароль.
 * </p>
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserDTO {
    private String name;
    private String surname;
    private String login;
    private String password;
}
