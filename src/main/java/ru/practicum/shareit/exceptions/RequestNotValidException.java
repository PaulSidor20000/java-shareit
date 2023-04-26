package ru.practicum.shareit.exceptions;

public class RequestNotValidException extends RuntimeException {

    public RequestNotValidException(String message) {
        super(message);
    }
}
