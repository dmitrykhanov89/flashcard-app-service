package com.sekhanov.flashcard.service.impl;

public class MailServiceException extends RuntimeException{
    public MailServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
