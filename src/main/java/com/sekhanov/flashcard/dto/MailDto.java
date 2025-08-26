package com.sekhanov.flashcard.dto;

import lombok.Data;

/**
 * DTO (Data Transfer Object) для передачи данных письма.
 * <p>
 * Используется для передачи информации об отправляемом письме
 * от контроллера к сервису отправки писем.
 * </p>
 */
@Data
public class MailDto {
    private String email;
    private String subject;
    private String message;
}
