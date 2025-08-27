package com.sekhanov.flashcard.service;

public interface DailyEmailService {

    /**
     * Отправляет ежедневные письма всем подтверждённым пользователям.
     */
    void sendDailyEmails();
}
