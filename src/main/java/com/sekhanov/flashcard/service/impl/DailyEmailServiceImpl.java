package com.sekhanov.flashcard.service.impl;

import com.sekhanov.flashcard.entity.User;
import com.sekhanov.flashcard.repository.UserRepository;
import com.sekhanov.flashcard.service.DailyEmailService;
import com.sekhanov.flashcard.service.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DailyEmailServiceImpl implements DailyEmailService {

    private final UserRepository userRepository;
    private final MailService mailService;

    /**
     * Метод выполняется раз в день (каждый день в 09:00).
     * Получает всех подтверждённых пользователей и отправляет им простое письмо.
     */
    @Override
    @Scheduled(cron = "0 0 9 * * ?") // Каждый день в 09:00
    public void sendDailyEmails() {
        // Получаем всех пользователей с подтверждённым email
        List<User> confirmedUsers = userRepository.findAllByIsEmailConfirmedTrue();

        // Отправляем каждому пользователю простое письмо
        for (User user : confirmedUsers) {
            String message = "Привет, " + user.getName() + "! Это ежедневное уведомление от Flashcard App.";
            mailService.sendMail(user.getEmail(), "Ежедневное уведомление", message);
        }
    }
}
