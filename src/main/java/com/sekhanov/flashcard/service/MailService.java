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
}
