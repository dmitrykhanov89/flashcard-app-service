package com.sekhanov.flashcard.service.impl;

import com.sekhanov.flashcard.dto.CreateUserDTO;
import com.sekhanov.flashcard.dto.UserDTO;
import com.sekhanov.flashcard.entity.User;
import com.sekhanov.flashcard.repository.UserRepository;
import com.sekhanov.flashcard.service.MailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.Optional;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private MailService mailService;
    @InjectMocks
    private UserServiceImpl userService;

    private CreateUserDTO createUserDTO;

    @BeforeEach
    void setUp() {
        createUserDTO = new CreateUserDTO("Ivan", "Petrov", "ivan", "ivan@example.com", "plainPass");
    }

    @Test
    void createUser_withValidData_shouldSaveUserAndSendEmail() {
        when(passwordEncoder.encode(createUserDTO.getPassword())).thenReturn("encodedPass");
        User user = makeUser(1L, "Ivan", "ivan@example.com", "encodedPass");
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDTO result = userService.createUser(createUserDTO);

        assertThat(result).extracting(UserDTO::getId, UserDTO::getName, UserDTO::getEmail).containsExactly(1L, "Ivan", "ivan@example.com");
        verify(passwordEncoder).encode(createUserDTO.getPassword());
        verify(userRepository).save(any(User.class));
        verify(mailService).sendMail(eq("ivan@example.com"), contains("Подтверждение"), contains("http://localhost:8080"));
    }

    @Test
    void confirmEmail_withValidToken_shouldConfirmUserAndReturnUserDTO() {
        User user = makeUser(2L, "Test", "test@test.com", "pass");
        user.setConfirmationToken("token123");
        user.setIsEmailConfirmed(false);
        when(userRepository.findByConfirmationToken("token123")).thenReturn(Optional.of(user));

        UserDTO result = userService.confirmEmail("token123");

        assertThat(result).isNotNull();
        assertThat(user.getIsEmailConfirmed()).isTrue();
        assertThat(user.getConfirmationToken()).isNull();
        verify(userRepository).save(user);
    }

    @Test
    void confirmEmail_withInvalidToken_shouldReturnNull() {
        when(userRepository.findByConfirmationToken("bad")).thenReturn(Optional.empty());
        assertThat(userService.confirmEmail("bad")).isNull();
        verify(userRepository, never()).save(any());
    }

    @Test
    void findCurrentUserEntity_whenAuthenticated_shouldReturnUser() {
        User user = makeUser(3L, "Ivan", "ivan@example.com", "pass");
        when(userRepository.findByLogin("ivan")).thenReturn(user);

        SecurityContextHolder.getContext().setAuthentication(new TestingAuthenticationToken("ivan", "password", "ROLE_USER"));

        assertThat(userService.findCurrentUserEntity()).isPresent().get().extracting(User::getLogin).isEqualTo("ivan");
    }

    @Test
    void findCurrentUserEntity_whenAnonymous_shouldReturnEmpty() {
        SecurityContextHolder.clearContext();
        assertThat(userService.findCurrentUserEntity()).isEmpty();
    }

    // ---------------- фабрики ----------------
    private User makeUser(Long id, String name, String email, String password) {
        User u = new User();
        u.setId(id);
        u.setName(name);
        u.setLogin(name.toLowerCase());
        u.setEmail(email);
        u.setPassword(password);
        u.setConfirmationToken(UUID.randomUUID().toString());
        u.setIsEmailConfirmed(false);
        return u;
    }
}
