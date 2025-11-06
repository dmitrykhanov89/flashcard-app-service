package com.sekhanov.flashcard.controller;

import com.sekhanov.flashcard.dto.MailDto;
import com.sekhanov.flashcard.service.MailService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST-контроллер для работы с отправкой писем.
 * <p>
 * Предоставляет конечные точки (endpoints) для отправки электронных писем
 * через сервис {@link MailService}.
 * </p>
 */
@RestController
@RequestMapping("/api/mail")
@RequiredArgsConstructor
public class MailController {

    private final MailService mailService;

    /**
     * Отправляет электронное письмо на указанный адрес.
     *
     * @param mailDto DTO с данными письма (адрес, тема, сообщение)
     * @return {@link ResponseEntity} с сообщением об успешной отправке
     */
    @PostMapping("/send")
    public ResponseEntity<String> sendEmail(@RequestBody MailDto mailDto) throws MessagingException {
        mailService.sendMail(mailDto.getEmail(), mailDto.getSubject(), mailDto.getMessage());
        return ResponseEntity.ok("Email отправлен!");
    }
}

