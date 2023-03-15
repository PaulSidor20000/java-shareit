package ru.practicum.shareit.exceptions;

public class EmailDuplicateException extends RuntimeException {
    public EmailDuplicateException() {
    }

    public EmailDuplicateException(String message) {
        super(message);
    }
}
