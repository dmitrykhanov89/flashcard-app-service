package com.sekhanov.flashcard.service.impl;

import com.sekhanov.flashcard.entity.User;
import com.sekhanov.flashcard.repository.UserRepository;
import com.sekhanov.flashcard.service.DailyEmailService;
import com.sekhanov.flashcard.service.MailService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DailyEmailServiceImpl implements DailyEmailService {

    private final UserRepository userRepository;
    private final MailService mailService;

    /**
     * Плановое задание: выполняется 31 декабря 09:00.
     * Отправляет простое письмо всем пользователям с подтверждённым email.
     */
    @Override
    @Scheduled(cron = "0 0 9 31 12 ?")
    public void sendDailyEmails() {
        log.info("Запуск рассылки писем подтверждённым пользователям.");
        List<User> confirmedUsers = userRepository.findAllByIsEmailConfirmedTrue();
        log.debug("Найдено {} подтверждённых пользователей для рассылки.", confirmedUsers.size());

        for (User user : confirmedUsers) {
            String message = String.format(
                    "Привет, %s! Поздравляем с наступающим новым годом! От Flashcard App.",
                    user.getName()
            );
            try {
                mailService.sendMail(user.getEmail(), "Новогоднее поздравление", message);
                log.info("Письмо успешно отправлено пользователю с email={}", user.getEmail());
            } catch (MessagingException e) {
                log.error("Ошибка при отправке письма пользователю с email={}: {}", user.getEmail(), e.getMessage());
            }
        }
        log.info("Рассылка завершена. Отправлено писем: {}", confirmedUsers.size());
    }
}
