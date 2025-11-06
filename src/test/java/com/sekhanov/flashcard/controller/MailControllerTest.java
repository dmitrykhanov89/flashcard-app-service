package com.sekhanov.flashcard.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sekhanov.flashcard.dto.MailDto;
import com.sekhanov.flashcard.service.MailService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = MailController.class)
@AutoConfigureMockMvc(addFilters = false)
class MailControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private MailService mailService;

    @Test
    void sendEmail_whenValidMailDto_shouldReturnOk() throws Exception {
        MailDto mailDto = new MailDto();
        mailDto.setEmail("test@example.com");
        mailDto.setSubject("Test Subject");
        mailDto.setMessage("Test message");

        Mockito.doNothing().when(mailService).sendMail(anyString(), anyString(), anyString());

        mockMvc.perform(post("/api/mail/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mailDto)))
                .andExpectAll(status().isOk(), content().string("Email отправлен!"));
    }
}
