package com.sekhanov.flashcard.controller;

import com.sekhanov.flashcard.dto.UserDTO;
import com.sekhanov.flashcard.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@WebMvcTest(controllers = UserController.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private UserService userService;

    @Test
    void getCurrentUser_whenAuthenticated_shouldReturnUser() throws Exception {
        UserDTO userDTO = new UserDTO(1L, "Ivan", "Petrov", "ivan123", "ivan@example.com");
        when(userService.getCurrentUser()).thenReturn(userDTO);

        mockMvc.perform(get("/api/users/currentUser")).andExpectAll(status().isOk(), content().contentType(MediaType.APPLICATION_JSON),
                jsonPath("$.id").value(1L),
                jsonPath("$.name").value("Ivan"),
                jsonPath("$.surname").value("Petrov"),
                jsonPath("$.login").value("ivan123"),
                jsonPath("$.email").value("ivan@example.com"));
    }

    @Test
    void getCurrentUser_whenNotAuthenticated_shouldThrowException() throws Exception {
        when(userService.getCurrentUser()).thenReturn(null);
        Exception exception = assertThrows(Exception.class, () -> mockMvc.perform(get("/api/users/currentUser")).andReturn());

        // unwrap, чтобы достать RuntimeException
        Throwable cause = exception.getCause();
        while (cause != null && !(cause instanceof RuntimeException)) {
            cause = cause.getCause();
        }
        assertNotNull(cause, "Ожидался RuntimeException");
        assertEquals("Пользователь не аутентифицирован", cause.getMessage());
    }
}
