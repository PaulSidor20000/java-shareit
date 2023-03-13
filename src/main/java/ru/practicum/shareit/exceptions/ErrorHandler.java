package ru.practicum.shareit.exceptions;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.HashMap;


@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse<Map<String, String>> validationHandler(MethodArgumentNotValidException errors) {
        return new ErrorResponse<>(errors.getFieldErrors()
                .stream()
                .collect(HashMap::new,
                        (map, fieldError) ->
                                map.put(fieldError.getField(), fieldError.getDefaultMessage()),
                        HashMap::putAll)
        );
    }

    @Getter
    private static class ErrorResponse<T> {
        T errors;

        public ErrorResponse(T errors) {
            this.errors = errors;
        }
    }

}
