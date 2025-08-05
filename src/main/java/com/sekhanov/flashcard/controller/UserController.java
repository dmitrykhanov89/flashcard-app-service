package com.sekhanov.flashcard.controller;

import com.sekhanov.flashcard.dto.UserDTO;
import com.sekhanov.flashcard.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Контроллер для обработки запросов, связанных с пользователями.
 */
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * Возвращает информацию о текущем аутентифицированном пользователе.
     *
     * @return {@link UserDTO} с данными пользователя
     * @throws RuntimeException если пользователь не аутентифицирован
     */
    @GetMapping("/currentUser")
    public UserDTO getCurrentUser() {
        UserDTO currentUser = userService.getCurrentUser();
        if (currentUser == null) {
            throw new RuntimeException("Пользователь не аутентифицирован");
        }
        return currentUser;
    }
}
