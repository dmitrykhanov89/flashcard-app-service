package com.sekhanov.flashcard.config;

import com.sekhanov.flashcard.service.JwtService;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtFilterTest {

    @Mock
    private JwtService jwtService;
    @Mock
    private UserDetailsService userDetailsService;
    @InjectMocks
    private JwtFilter jwtFilter;

    @BeforeEach
    void setup() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void doFilterInternal_validToken_setsAuthentication() throws ServletException, IOException {
        String token = "validToken";
        MockHttpServletRequest req = new MockHttpServletRequest();
        req.addHeader("Authorization", "Bearer " + token);
        MockHttpServletResponse res = new MockHttpServletResponse();
        UserDetails userDetails = new User("u1", "password", List.of());

        when(jwtService.validateToken(token)).thenReturn(true);
        when(jwtService.extractLogin(token)).thenReturn("u1");
        when(userDetailsService.loadUserByUsername("u1")).thenReturn(userDetails);
        when(jwtService.extractExpiration(token)).thenReturn(new Date(System.currentTimeMillis() + 60_000));

        jwtFilter.doFilterInternal(req, res, (request, response) -> {});

        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals("u1", SecurityContextHolder.getContext().getAuthentication().getName());
        verify(jwtService).validateToken(token);
        verify(userDetailsService).loadUserByUsername("u1");
    }

    @Test
    void doFilterInternal_noToken_doesNotSetAuthentication() throws ServletException, IOException {
        MockHttpServletRequest req = new MockHttpServletRequest();
        MockHttpServletResponse res = new MockHttpServletResponse();

        jwtFilter.doFilterInternal(req, res, (request, response) -> {});

        assertNull(SecurityContextHolder.getContext().getAuthentication());
        verifyNoInteractions(jwtService, userDetailsService);
    }
}
