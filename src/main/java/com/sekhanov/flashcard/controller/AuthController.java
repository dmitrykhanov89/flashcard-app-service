package com.sekhanov.flashcard.controller;

import com.sekhanov.flashcard.dto.AuthRequest;
import com.sekhanov.flashcard.dto.AuthResponse;
import com.sekhanov.flashcard.dto.CreateUserDTO;
import com.sekhanov.flashcard.dto.UserDTO;
import com.sekhanov.flashcard.service.JwtService;
import com.sekhanov.flashcard.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

/**
 * Контроллер для управления аутентификацией и регистрацией пользователей.
 * <p>
 * Обрабатывает HTTP-запросы для входа в систему (логина) и регистрации новых пользователей.
 * Выполняет проверку учетных данных, генерирует JWT-токены и взаимодействует с сервисным слоем
 * для создания и получения данных пользователей.
 * </p>
 *
 * Все эндпоинты находятся по пути {@code /api/auth}.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final JwtService jwtService;
    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    /**
     * Обрабатывает POST-запрос на аутентификацию пользователя.
     * <p>
     * Получает логин и пароль из тела запроса, проверяет их корректность,
     * и в случае успешной аутентификации возвращает JWT-токен.
     * Если логин или пароль неверны — возвращает ошибку 401 Unauthorized с сообщением.
     * </p>
     *
     * @param req объект {@link AuthRequest}, содержащий логин и пароль пользователя.
     * @return {@link ResponseEntity} с объектом {@link AuthResponse}:
     *         - при успешной аутентификации HTTP 200 OK и токен;
     *         - при ошибке аутентификации HTTP 401 Unauthorized и сообщение об ошибке.
     */
    @PostMapping("/login")
    public String login(@RequestBody AuthRequest req) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(req.getLogin(), req.getPassword()));
        return jwtService.generateToken(req.getLogin());
    }

    /**
     * Обрабатывает POST-запрос на регистрацию нового пользователя.
     * <p>
     * Принимает данные нового пользователя в виде {@link CreateUserDTO},
     * создает пользователя через сервис {@link UserService} и возвращает
     * DTO созданного пользователя с HTTP статусом 201 Created.
     * </p>
     *
     * @param createUserDTO объект {@link CreateUserDTO}, содержащий данные для создания пользователя.
     * @return {@link ResponseEntity} с объектом {@link UserDTO} и статусом 201 Created.
     */
    @PostMapping("/registration")
    public ResponseEntity<UserDTO> registration(@RequestBody CreateUserDTO createUserDTO) {
        UserDTO userDTO = userService.createUser(createUserDTO);
        return new ResponseEntity<>(userDTO, HttpStatus.CREATED);
    }
}
