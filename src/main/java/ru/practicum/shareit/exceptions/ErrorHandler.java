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
    public static final String UNKNOWN_STATE = "Unknown state: %s";
    public static final String FAILED_ITEM_ID = "Failed Item id: %s";
    public static final String FAILED_USER_ID = "Failed user id: %s";
    public static final String FAILED_OWNER_ID = "Failed owner id: %s";
    public static final String FAILED_BOOKING_ID = "Failed booking id: %s";
    public static final String DUPLICATED_EMAIL = "Duplicated email found: %s";
    public static final String ENTITY_NOT_FOUND_MESSAGE = "Failed to find an entity: %s in database";

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

    @ExceptionHandler({
            ValidationException.class,
            UnknownStateException.class,
            IllegalArgumentException.class
    })
    public ResponseEntity<Map<String, String>> validationHandler(RuntimeException error) {
        log.warn(LOG_ERROR, error.getMessage());
        return ResponseEntity.status(400).body(Map.of(A_ERROR, error.getMessage()));
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> validationEntityHandler(EntityNotFoundException error) {
        log.warn(LOG_ERROR, error.getMessage());
        return ResponseEntity.status(404).body(Map.of(A_ERROR, error.getMessage()));
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> validationBookingMatchToEntityHandler(BookingNotMatchException error) {
        log.warn(LOG_ERROR, error.getMessage());
        return ResponseEntity.status(404).body(Map.of(A_ERROR, error.getMessage()));
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> validationEmailHandler(EmailDuplicateException error) {
        log.warn(LOG_ERROR, error.getMessage());
        return ResponseEntity.status(409).body(Map.of(A_ERROR, error.getMessage()));
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> otherServerErrorsHandler(Throwable error) {
        log.warn(LOG_ERROR, error.getMessage());
        return ResponseEntity.status(500).body(Map.of(SERVER_ERROR, error.getMessage()));
    }

}
