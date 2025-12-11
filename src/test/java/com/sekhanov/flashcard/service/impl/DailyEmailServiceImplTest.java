package com.sekhanov.flashcard.service.impl;

import com.sekhanov.flashcard.entity.User;
import com.sekhanov.flashcard.repository.UserRepository;
import com.sekhanov.flashcard.service.MailService;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DailyEmailServiceImplTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private MailService mailService;
    @InjectMocks
    private DailyEmailServiceImpl dailyEmailService;

    private User makeUser(Long id, String name, String email) {
        User user = new User();
        user.setId(id);
        user.setName(name);
        user.setEmail(email);
        user.setIsEmailConfirmed(true);
        return user;
    }

    @Test
    void sendDailyEmails_withConfirmedUsers_shouldSendEmails() throws MessagingException{
        List<User> confirmedUsers = List.of(makeUser(1L, "Ivan", "ivan@example.com"),
                makeUser(2L, "Anna", "anna@example.com"));
        when(userRepository.findAllByIsEmailConfirmedTrue()).thenReturn(confirmedUsers);

        dailyEmailService.sendDailyEmails();

        // проверяем, что каждому пользователю отправлено письмо с правильным именем
        confirmedUsers.forEach(u -> {
            try {
                verify(mailService).sendMail(eq(u.getEmail()), eq("Ежедневное уведомление"), contains(u.getName()));
            } catch (MessagingException e) {
                throw new RuntimeException(e);
            }});
        verify(mailService, times(2)).sendMail(anyString(), anyString(), anyString());
        verify(userRepository).findAllByIsEmailConfirmedTrue();
    }

    @Test
    void sendDailyEmails_withNoConfirmedUsers_shouldNotSendEmails() throws MessagingException {
        when(userRepository.findAllByIsEmailConfirmedTrue()).thenReturn(List.of());

        dailyEmailService.sendDailyEmails();

        verify(mailService, never()).sendMail(anyString(), anyString(), anyString());
        verify(userRepository).findAllByIsEmailConfirmedTrue();
    }
}
