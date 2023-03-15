package ru.practicum.shareit.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.*;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    // @ResponseStatus(HttpStatus.BAD_REQUEST) //    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<Map<String, String>> validationHandler(MethodArgumentNotValidException errors) {
        if (Objects.equals(Objects.requireNonNull(errors.getFieldError()).getDefaultMessage(), "email exists in database")) {
            return ResponseEntity.status(409).body(errors.getFieldErrors()
                    .stream()
                    .collect(HashMap::new,
                            (errorsSummery, fieldError) ->
                                    errorsSummery.put(fieldError.getField(), fieldError.getDefaultMessage()),
                            HashMap::putAll));
        }
        return ResponseEntity.status(400).body(errors.getFieldErrors()
                .stream()
                .collect(HashMap::new,
                        (errorsSummery, fieldError) ->
                                errorsSummery.put(fieldError.getField(), fieldError.getDefaultMessage()),
                        HashMap::putAll));
    }

    @ExceptionHandler({EmailDuplicateException.class, MissingObjectException.class})
    public ResponseEntity<Map<String, String>> validationHandler(RuntimeException errors) {
            return ResponseEntity.status(409).body(Map.of("error", errors.getMessage()));
    }
}
