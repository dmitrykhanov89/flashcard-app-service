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
     * Плановое задание: выполняется каждый день в 09:00.
     * Отправляет простое письмо всем пользователям с подтверждённым email.
     */
    @Override
    @Scheduled(cron = "0 0 9 * * ?") // Каждый день в 09:00
    public void sendDailyEmails() {
        log.info("Запуск ежедневной рассылки писем подтверждённым пользователям.");
        List<User> confirmedUsers = userRepository.findAllByIsEmailConfirmedTrue();
        log.debug("Найдено {} подтверждённых пользователей для рассылки.", confirmedUsers.size());

        for (User user : confirmedUsers) {
            String message = String.format(
                    "Привет, %s! Это ежедневное уведомление от Flashcard App.",
                    user.getName()
            );
            try {
                mailService.sendMail(user.getEmail(), "Ежедневное уведомление", message);
                log.info("Письмо успешно отправлено пользователю с email={}", user.getEmail());
            } catch (MessagingException e) {
                log.error("Ошибка при отправке письма пользователю с email={}: {}", user.getEmail(), e.getMessage());
            }
        }
        log.info("Ежедневная рассылка завершена. Отправлено писем: {}", confirmedUsers.size());
    }
}