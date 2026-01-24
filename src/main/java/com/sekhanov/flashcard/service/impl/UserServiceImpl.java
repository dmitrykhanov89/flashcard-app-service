package com.sekhanov.flashcard.service.impl;

import com.sekhanov.flashcard.config.SecurityConfig;
import com.sekhanov.flashcard.dto.CreateUserDTO;
import com.sekhanov.flashcard.dto.UserDTO;
import com.sekhanov.flashcard.entity.User;
import com.sekhanov.flashcard.repository.UserRepository;
import com.sekhanov.flashcard.service.MailService;
import com.sekhanov.flashcard.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;
import java.util.UUID;

/**
 * <p>
 * Реализация сервиса {@link UserService}, предоставляющая функциональность для управления пользователями.
 * </p>
 *
 * <p>Основные возможности:</p>
 * <ul>
 *     <li>Создание нового пользователя с хэшированием пароля</li>
 *     <li>Получение текущего аутентифицированного пользователя в виде DTO или сущности</li>
 *     <li>Преобразование сущности {@link User} в {@link UserDTO}</li>
 * </ul>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;

    @Value("${app.host}")
    private String host;

    @Override
    @Transactional
    public UserDTO createUser(CreateUserDTO dto) {
        log.debug("Создание пользователя с логином '{}' и email '{}'", dto.getLogin(), dto.getEmail());

        if (userRepository.findByLogin(dto.getLogin()) != null) {
            log.warn("Пользователь с логином '{}' уже существует", dto.getLogin());
            throw new IllegalArgumentException("Пользователь с таким логином уже существует");
        }

        User user = new User();
        user.setName(dto.getName());
        user.setSurname(dto.getSurname());
        user.setLogin(dto.getLogin());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setIsEmailConfirmed(false);

        String token = UUID.randomUUID().toString();
        user.setConfirmationToken(token);

        user = userRepository.save(user);
        log.info("Создан новый пользователь id={} login={} email={}", user.getId(), user.getLogin(), user.getEmail());

        try {
            mailService.sendRegistrationMail(dto.getEmail(), token, host);
            log.debug("Письмо с подтверждением отправлено для email={}", dto.getEmail());
        } catch (Exception e) {
            log.error("Ошибка при отправке письма для email={}", dto.getEmail(), e);
            throw new MailServiceException("Не удалось отправить письмо подтверждения", e);
        }

        return toDTO(user);
    }

    @Override
    public UserDTO getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            log.warn("Попытка получить текущего пользователя без авторизации");
            throw new IllegalStateException("Пользователь не аутентифицирован");
        }

        SecurityConfig.CustomUserDetails userDetails = (SecurityConfig.CustomUserDetails) auth.getPrincipal();
        log.info("Текущий пользователь: id={}, login={}", userDetails.getId(), userDetails.getUsername());

        User user = userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new IllegalStateException("Пользователь не найден"));

        return toDTO(user);
    }

    @Override
    public Optional<User> findCurrentUserEntity() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getName())) {
            log.debug("Пользователь не аутентифицирован — возвращаем Optional.empty()");
            return Optional.empty();
        }

        String login = auth.getName();
        log.debug("Получение сущности пользователя с логином '{}'", login);
        return Optional.ofNullable(userRepository.findByLogin(login));
    }

    @Override
    @Transactional
    public UserDTO confirmEmail(String token) {
        log.debug("Подтверждение email по токену {}", token);

        Optional<User> optionalUser = userRepository.findByConfirmationToken(token);
        if (optionalUser.isEmpty()) {
            log.warn("Неверный токен подтверждения: {}", token);
            throw new IllegalArgumentException("Неверный токен подтверждения");
        }

        User user = optionalUser.get();
        user.setIsEmailConfirmed(true);
        user.setConfirmationToken(null);
        userRepository.save(user);

        log.info("Email успешно подтверждён для пользователя login={}", user.getLogin());
        return toDTO(user);
    }

    private UserDTO toDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setSurname(user.getSurname());
        dto.setLogin(user.getLogin());
        dto.setEmail(user.getEmail());
        return dto;
    }
}