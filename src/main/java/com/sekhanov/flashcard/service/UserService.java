package com.sekhanov.flashcard.service;

import com.sekhanov.flashcard.dto.CreateUserDTO;
import com.sekhanov.flashcard.dto.UserDTO;
import com.sekhanov.flashcard.entity.User;
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

    /**
     * Получить DTO текущего аутентифицированного пользователя.
     *
     * @return UserDTO текущего пользователя или null, если пользователь не аутентифицирован.
     */
    UserDTO getCurrentUser();

    /**
     * Возвращает сущность текущего аутентифицированного пользователя.
     *
     * @return Optional, содержащий сущность {@link User}, если пользователь аутентифицирован;
     *         пустой Optional, если пользователь не найден или не аутентифицирован.
     */
    Optional<User> findCurrentUserEntity();
}

