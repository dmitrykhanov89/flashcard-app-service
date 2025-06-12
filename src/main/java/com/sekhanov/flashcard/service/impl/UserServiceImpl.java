package com.sekhanov.flashcard.service.impl;

import com.sekhanov.flashcard.dto.CreateUserDTO;
import com.sekhanov.flashcard.dto.UserDTO;
import com.sekhanov.flashcard.entity.User;
import com.sekhanov.flashcard.entity.WordList;
import com.sekhanov.flashcard.repository.UserRepository;
import com.sekhanov.flashcard.repository.WordListRepository;
import com.sekhanov.flashcard.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * <p>
 * Предоставляет функциональность для создания новых пользователей и преобразования сущности {@link com.sekhanov.flashcard.entity.User}
 * в DTO-объект {@link com.sekhanov.flashcard.dto.UserDTO}.
 * </p>
 *
 * <p>Основные возможности:</p>
 * <ul>
 *     <li>Создание нового пользователя с хэшированием пароля</li>
 *     <li>Преобразование сущности User в безопасный формат для передачи через API (UserDTO)</li>
 * </ul>
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public UserDTO createUser(CreateUserDTO createUserDTO) {
        User user = new User();
        user.setName(createUserDTO.getName());
        user.setSurname(createUserDTO.getSurname());
        user.setLogin(createUserDTO.getLogin());
        user.setPassword(passwordEncoder.encode(createUserDTO.getPassword()));
        user = userRepository.save(user);
        return toDTO(user);
    }

    // Преобразование User в UserDTO
    private UserDTO toDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setSurname(user.getSurname());
        dto.setLogin(user.getLogin());
        dto.setWordLists(user.getWordLists());
        return dto;
    }

}
