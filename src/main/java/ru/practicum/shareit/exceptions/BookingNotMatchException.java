package ru.practicum.shareit.exceptions;

public class BookingNotMatchException extends RuntimeException {
    public BookingNotMatchException() {
    }

    public BookingNotMatchException(String message) {
        super(message);
    }
}
