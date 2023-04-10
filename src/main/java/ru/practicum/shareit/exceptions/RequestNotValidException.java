package ru.practicum.shareit.exceptions;

public class RequestNotValidException extends RuntimeException{
    public RequestNotValidException(){}
    public RequestNotValidException(String message){
        super(message);
    }

}
