package com.sekhanov.flashcard.service;

import jakarta.mail.MessagingException;

/**
 * Сервисный интерфейс для отправки электронных писем.
 * <p>
 * Определяет контракт для отправки простого сообщения на указанный адрес
 * электронной почты с заданной темой и текстом.
 * </p>
 */
public interface MailService {
    /**
     * Отправляет письмо на указанный адрес электронной почты.
     *
     * @param emailAddress адрес получателя
     * @param subject      тема письма
     * @param message      текст письма
     */
    void sendMail(String emailAddress, String subject, String message) throws MessagingException;
    /**
     * Отправляет письмо для подтверждения регистрации.
     *
     * @param emailAddress адрес получателя
     * @param token        токен подтверждения
     * @param host         базовый хост
     * @throws MessagingException если произошла ошибка при отправке
     */
    default void sendRegistrationMail(String emailAddress, String token, String host) throws MessagingException {
        String confirmLink = host + "/api/auth/confirm-email?token=" + token;
        String message = "<p>Для подтверждения email перейдите по ссылке:</p>" +
                "<a href=\"" + confirmLink + "\">Подтвердить email</a>";
        sendMail(emailAddress, "Подтверждение email", message);
    }
}
