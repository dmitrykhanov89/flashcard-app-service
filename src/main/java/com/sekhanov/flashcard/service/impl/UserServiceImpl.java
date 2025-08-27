package com.sekhanov.flashcard.service.impl;

import com.sekhanov.flashcard.config.SecurityConfig;
import com.sekhanov.flashcard.dto.CreateUserDTO;
import com.sekhanov.flashcard.dto.UserDTO;
import com.sekhanov.flashcard.entity.User;
import com.sekhanov.flashcard.repository.UserRepository;
import com.sekhanov.flashcard.service.MailService;
import com.sekhanov.flashcard.service.UserService;
import lombok.RequiredArgsConstructor;
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
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;

    @Override
    @Transactional
    public UserDTO createUser(CreateUserDTO createUserDTO) {
        User user = new User();
        user.setName(createUserDTO.getName());
        user.setSurname(createUserDTO.getSurname());
        user.setLogin(createUserDTO.getLogin());
        user.setEmail(createUserDTO.getEmail());
        user.setPassword(passwordEncoder.encode(createUserDTO.getPassword()));
        user.setIsEmailConfirmed(false);
        // Генерация токена подтверждения email
        String token = UUID.randomUUID().toString();
        user.setConfirmationToken(token);
        user = userRepository.save(user);
        // Отправка письма с ссылкой подтверждения
        String confirmLink = "http://localhost:8080/api/auth/confirm-email?token=" + token;
        String message = "Привет!<br>" +
                "Для подтверждения email перейдите по ссылке: " +
                "<a href=\"" + confirmLink + "\">" + confirmLink + "</a>";
        mailService.sendMail(user.getEmail(), "Подтверждение email", message);
        return toDTO(user);
    }

    @Override
    public UserDTO getCurrentUser() {
        SecurityConfig.CustomUserDetails currentUser = (SecurityConfig.CustomUserDetails) SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getPrincipal();

        UserDTO dto = new UserDTO();
        dto.setId(currentUser.getId());
        dto.setLogin(currentUser.getUsername());
        dto.setName(currentUser.getName());
        dto.setSurname(currentUser.getSurname());
        return dto;
    }

    @Override
    public Optional<User> findCurrentUserEntity() {
        return findAuthenticatedUser();
    }

    private Optional<User> findAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getName().equals("anonymousUser")) {
            return Optional.empty();
        }
        String login = authentication.getName();
        return Optional.ofNullable(userRepository.findByLogin(login));
    }

    @Override
    @Transactional
    public UserDTO confirmEmail(String token) {
        Optional<User> optionalUser = userRepository.findByConfirmationToken(token);
        if (optionalUser.isEmpty()) {
            return null; // токен некорректный
        }
        User user = optionalUser.get();
        user.setIsEmailConfirmed(true);
        user.setConfirmationToken(null);
        userRepository.save(user);
        return toDTO(user);
    }

    // Преобразование User в UserDTO
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
