package com.sekhanov.flashcard.config;

import com.sekhanov.flashcard.entity.User;
import com.sekhanov.flashcard.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class SecurityConfigTest {
    @Autowired
    private SecurityConfig securityConfig;
    @Autowired
    private UserRepository userRepository;

    @Test
    void userDetailsService_existingUser_returnsUserDetails() {
        User user = new User();
        user.setLogin("u1"); user.setEmail("u1@test"); user.setPassword("p");
        userRepository.saveAndFlush(user); // обязательно flush

        UserDetails details = securityConfig.userDetailsService().loadUserByUsername("u1");
        assertEquals("u1", details.getUsername());
        assertEquals("p", details.getPassword());
    }

    @Test
    void userDetailsService_nonexistentUser_throwsUsernameNotFoundException() {
        assertThrows(UsernameNotFoundException.class, () -> securityConfig.userDetailsService().loadUserByUsername("nonexistent"));
    }

    @Test
    void jwtFilter_passwordEncoder_userDetailsService_beansAreLoaded_returnsNotNul() {
        assertNotNull(securityConfig.jwtFilter());
        assertNotNull(securityConfig.passwordEncoder());
        assertNotNull(securityConfig.userDetailsService());
    }
}
