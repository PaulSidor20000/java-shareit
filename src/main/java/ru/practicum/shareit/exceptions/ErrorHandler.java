package ru.practicum.shareit.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.*;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> validationAnnotationHandler(MethodArgumentNotValidException error) {
        log.warn("error: {}", error.getMessage());
        return ResponseEntity.status(400).body(error.getFieldErrors().stream()
                .collect(
                        HashMap::new,
                        (errors, fieldError) -> errors.put(fieldError.getField(), fieldError.getDefaultMessage()),
                        HashMap::putAll
                ));
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> validationEmailHandler(EmailDuplicateException error) {
        log.warn("error: {}", error.getMessage());
        return ResponseEntity.status(409).body(Map.of("error", error.getMessage()));
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> validationObjectHandler(MissingObjectException error) {
        log.warn("error: {}", error.getMessage());
        return ResponseEntity.status(404).body(Map.of("error", error.getMessage()));
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> validationHeaderHandler(MissingRequestHeaderException error) {
        log.warn("error: {}", error.getMessage());
        return ResponseEntity.status(400).body(Map.of(error.getHeaderName(), Objects.requireNonNull(error.getMessage())));
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> otherServerErrorsHandler(Throwable error) {
        log.warn("error: {}", error.getMessage());
        return ResponseEntity.status(500).body(Map.of("Server error", error.getMessage()));
    }

}
