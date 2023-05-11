package ru.practicum.shareit.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {
    public static final String A_ERROR = "error";
    public static final String LOG_ERROR = "Error message: {}";
    public static final String SERVER_ERROR = "Server error:";

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> validationAnnotationHandler(MethodArgumentNotValidException error) {
        log.warn(LOG_ERROR, error.getMessage());
        return ResponseEntity.status(400).body(error.getFieldErrors().stream()
                .collect(Collectors.toMap(FieldError::getField, Objects.requireNonNull(FieldError::getDefaultMessage))));
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> validationHeaderHandler(MissingRequestHeaderException error) {
        log.warn(LOG_ERROR, error.getMessage());
        return ResponseEntity.status(400).body(Map.of(error.getHeaderName(), Objects.requireNonNull(error.getMessage())));
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> validationHandler(RuntimeException error) {
        log.warn(LOG_ERROR, error.getMessage());
        return ResponseEntity.status(400).body(Map.of(A_ERROR, error.getMessage()));
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> otherServerErrorsHandler(Throwable error) {
        log.warn(LOG_ERROR, error.getMessage());
        return ResponseEntity.status(500).body(Map.of(SERVER_ERROR, error.getMessage()));
    }

}
