package com.sekhanov.flashcard.service.impl;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MailServiceImplDevTest {
    @Mock
    private JavaMailSender mailSender;
    @InjectMocks
    private MailServiceImplDev mailService;

    @Test
    void sendMail_whenCalled_shouldSendMimeMessage() throws MessagingException {
        String email = "test@example.com";
        String subject = "Hello";
        String body = "<p>Body</p>";
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        mailService.sendMail(email, subject, body);

        verify(mailSender).send(mimeMessage);
    }

    @Test
    void sendMail_whenCalled_shouldNotSendNullMimeMessage() throws MessagingException{
        MimeMessage mimeMessage = mock(MimeMessage.class);
        when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

        mailService.sendMail("a@b.com", "Subj", "Body");

        MimeMessage sent = captureMimeMessage();
        assertThat(sent).isNotNull();
    }

    // ---------------- вспомогательный метод ----------------
    private MimeMessage captureMimeMessage() {
        ArgumentCaptor<MimeMessage> captor = ArgumentCaptor.forClass(MimeMessage.class);
        verify(mailSender).send(captor.capture());
        return captor.getValue();
    }
}
