package com.sekhanov.flashcard.service.impl;

import com.sekhanov.flashcard.service.MailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {

    private final JavaMailSender mailSender;

    @Override
    public void sendMail(String emailAddress, String subject, String message) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

            helper.setTo(emailAddress);
            helper.setSubject(subject);
            helper.setText(message, true); // true = поддержка HTML

            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            throw new RuntimeException("Ошибка при отправке письма", e);
        }
    }
}
