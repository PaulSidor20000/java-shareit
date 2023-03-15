package ru.practicum.shareit.exceptions;

public class MissingObjectException extends RuntimeException {
    public MissingObjectException() {
    }

    public MissingObjectException(String message) {
        super(message);
    }
}
