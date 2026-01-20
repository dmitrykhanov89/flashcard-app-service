package com.sekhanov.flashcard.service.impl;

import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.CreateEmailResponse;
import com.sekhanov.flashcard.service.MailService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Profile("prod")
@RequiredArgsConstructor
public class ResendMailServiceImpl implements MailService {

    private Resend resend;

    @Value("${resend.api-key}")
    private String apiKey;

    @Value("${resend.sender}")
    private String sender;

    @PostConstruct
    public void init() {
        resend = new Resend(apiKey);
    }

    @Override
    public void sendMail(String emailAddress, String subject, String message) {

        log.info("[PROD] Sending mail via Resend to {}", emailAddress);

        CreateEmailOptions params = CreateEmailOptions.builder()
                .from(sender)
                .to(emailAddress)
                .subject(subject)
                .html(message)
                .build();

        try {
            CreateEmailResponse response = resend.emails().send(params);
            log.info("[PROD] Mail sent, id={}", response.getId());
        } catch (ResendException e) {
            log.error("[PROD] Failed to send mail", e);
            throw new RuntimeException("Failed to send mail via Resend", e);
        }
    }
}
