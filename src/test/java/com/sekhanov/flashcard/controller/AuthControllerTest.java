package com.sekhanov.flashcard.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sekhanov.flashcard.dto.AuthRequest;
import com.sekhanov.flashcard.dto.CreateUserDTO;
import com.sekhanov.flashcard.dto.UserDTO;
import com.sekhanov.flashcard.service.JwtService;
import com.sekhanov.flashcard.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private JwtService jwtService;
    @MockitoBean
    private UserService userService;
    @MockitoBean
    private AuthenticationManager authenticationManager;

    @Test
    void login_whenCredentialsAreValid_shouldReturnToken() throws Exception {
        AuthRequest authRequest = new AuthRequest();
        authRequest.setLogin("ivan123");
        authRequest.setPassword("password");

        Mockito.when(authenticationManager.authenticate(Mockito.any(UsernamePasswordAuthenticationToken.class))).thenReturn(null);
        Mockito.when(jwtService.generateToken(anyString())).thenReturn("fake-jwt-token");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpectAll(status().isOk(), content().string("fake-jwt-token"));
    }

    @Test
    void login_whenCredentialsAreInvalid_shouldReturnUnauthorized() throws Exception {
        AuthRequest authRequest = new AuthRequest();
        authRequest.setLogin("ivan123");
        authRequest.setPassword("wrong-password");

        Mockito.doThrow(BadCredentialsException.class).when(authenticationManager).authenticate(any(UsernamePasswordAuthenticationToken.class));

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void registration_whenValidCreateUserDTO_shouldReturnCreatedUser() throws Exception {
        CreateUserDTO createUserDTO = new CreateUserDTO("Ivan", "Petrov", "ivan123", "password", "ivan@example.com");
        UserDTO userDTO = new UserDTO(1L, "Ivan", "Petrov", "ivan123", "ivan@example.com");

        Mockito.when(userService.createUser(any(CreateUserDTO.class))).thenReturn(userDTO);

        mockMvc.perform(post("/api/auth/registration")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createUserDTO)))
                .andExpectAll(status().isCreated(),
                        jsonPath("$.id").value(1L),
                        jsonPath("$.name").value("Ivan"),
                        jsonPath("$.surname").value("Petrov"),
                        jsonPath("$.login").value("ivan123"),
                        jsonPath("$.email").value("ivan@example.com"));
    }

    @Test
    void confirmEmail_whenTokenIsValid_shouldReturnOk() throws Exception {
        Mockito.when(userService.confirmEmail("valid-token")).thenReturn(new UserDTO());

        mockMvc.perform(get("/api/auth/confirm-email")
                        .param("token", "valid-token"))
                .andExpectAll(status().isOk(), content().string("Email подтверждён успешно!"));
    }

    @Test
    void confirmEmail_whenTokenIsInvalid_shouldReturnBadRequest() throws Exception {
        Mockito.when(userService.confirmEmail("invalid-token")).thenReturn(null);

        mockMvc.perform(get("/api/auth/confirm-email")
                        .param("token", "invalid-token"))
                .andExpectAll(status().isBadRequest(), content().string("Некорректный или уже использованный токен"));
    }
}
