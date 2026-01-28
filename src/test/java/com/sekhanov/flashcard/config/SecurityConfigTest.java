package com.sekhanov.flashcard.config;

import com.sekhanov.flashcard.entity.User;
import com.sekhanov.flashcard.repository.UserRepository;
import com.sekhanov.flashcard.service.JwtService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SecurityConfigTest {
    private UserRepository userRepository;
    private JwtService jwtService;
    private List<String> allowedOrigins;
    private SecurityConfig securityConfig;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        jwtService = mock(JwtService.class);
        allowedOrigins = List.of("http://localhost:5173", "https://app.example.com");
        securityConfig = new SecurityConfig(allowedOrigins, userRepository, jwtService);
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

    @Test
    void corsConfigurationSource_usesConfiguredOriginsAndHeaders() {
        CorsConfigurationSource source = securityConfig.corsConfigurationSource();
        CorsConfiguration config = source.getCorsConfiguration(new MockHttpServletRequest());

        assertNotNull(config);
        assertEquals(allowedOrigins, config.getAllowedOrigins());
        assertEquals(List.of("GET","POST","PUT","PATCH","DELETE","OPTIONS"), config.getAllowedMethods());
        assertEquals(List.of("Authorization","Content-Type"), config.getAllowedHeaders());
        assertTrue(Boolean.TRUE.equals(config.getAllowCredentials()));
    }
}
