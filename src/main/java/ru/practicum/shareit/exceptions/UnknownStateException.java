package ru.practicum.shareit.exceptions;

public class UnknownStateException extends RuntimeException {
    public UnknownStateException() {
    }

    public UnknownStateException(String message) {
        super(message);
    }
}
