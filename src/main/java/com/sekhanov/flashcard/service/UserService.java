package com.sekhanov.flashcard.service;

import com.sekhanov.flashcard.dto.CreateUserDTO;
import com.sekhanov.flashcard.dto.UserDTO;

import java.util.List;
import java.util.Optional;

/**
 * Сервис для управления пользователями (User).
 */
public interface UserService {

    /**
     * Создает нового пользователя на основе переданных данных.
     *
     * @param createUserDTO DTO с данными нового пользователя
     * @return DTO созданного пользователя
     */
    UserDTO createUser(CreateUserDTO createUserDTO);

}

