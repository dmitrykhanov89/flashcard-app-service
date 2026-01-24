package com.sekhanov.flashcard.config;

import com.sekhanov.flashcard.entity.User;
import com.sekhanov.flashcard.repository.UserRepository;
import com.sekhanov.flashcard.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SecurityConfigTest {
    private UserRepository userRepository;
    private JwtService jwtService;
    private SecurityConfig securityConfig;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        jwtService = mock(JwtService.class);
        securityConfig = new SecurityConfig(userRepository, jwtService);
    }

    @Test
    void userDetailsService_existingUser_returnsUserDetails() {
        User user = new User();
        user.setId(42L);
        user.setLogin("u1");
        user.setPassword("p");
        when(userRepository.findByLogin("u1")).thenReturn(user);

        UserDetails details = securityConfig.userDetailsService().loadUserByUsername("u1");

        assertEquals("u1", details.getUsername());
        assertEquals("p", details.getPassword());
    }

    @Test
    void userDetailsService_nonexistentUser_throwsUsernameNotFoundException() {
        when(userRepository.findByLogin("missing")).thenReturn(null);

        assertThrows(UsernameNotFoundException.class,
                () -> securityConfig.userDetailsService().loadUserByUsername("missing"));
    }

    @Test
    void jwtFilter_passwordEncoder_userDetailsService_beansAreLoaded_returnsNotNull() {
        assertNotNull(securityConfig.jwtFilter());
        assertNotNull(securityConfig.passwordEncoder());
        assertNotNull(securityConfig.userDetailsService());
    }
}
