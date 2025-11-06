package com.sekhanov.flashcard.config;

import com.sekhanov.flashcard.entity.User;
import com.sekhanov.flashcard.repository.UserRepository;
import com.sekhanov.flashcard.service.JwtService;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import java.io.IOException;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class JwtFilterTest {
    @Autowired
    private JwtService jwtService;
    @Autowired
    private UserRepository userRepository;

    private JwtFilter jwtFilter;
    private User testUser;

    @BeforeEach
    void setup() {
        String uniqueLogin = "user" + System.currentTimeMillis();testUser = new User();testUser.setLogin(uniqueLogin);
        testUser.setPassword("pass");testUser.setEmail(uniqueLogin + "@test.com");testUser.setIsEmailConfirmed(true);
        testUser = userRepository.saveAndFlush(testUser);

        jwtFilter = new JwtFilter(jwtService, username -> {
            User u = userRepository.findByLogin(username);
            if (u == null) throw new RuntimeException("User not found");
            return new SecurityConfig.CustomUserDetails(u.getId(), u.getLogin(), u.getName(), u.getSurname(), u.getPassword(), List.of());
        });
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilterInternal_validToken_setsAuthentication() throws ServletException, IOException {
        String token = jwtService.generateToken(testUser.getLogin());
        MockHttpServletRequest req = new MockHttpServletRequest();
        MockHttpServletResponse res = new MockHttpServletResponse();
        req.addHeader("Authorization", "Bearer " + token);

        jwtFilter.doFilterInternal(req, res, (request, response) -> {});

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals(testUser.getLogin(), SecurityContextHolder.getContext().getAuthentication().getName());
    }

    @Test
    void doFilterInternal_noToken_doesNotSetAuthentication() throws ServletException, IOException {
        MockHttpServletRequest req = new MockHttpServletRequest();
        MockHttpServletResponse res = new MockHttpServletResponse();

        jwtFilter.doFilterInternal(req, res, (request, response) -> {});

        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }
}
