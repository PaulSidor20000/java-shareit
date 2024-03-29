package ru.practicum.shareit.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.Objects;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {
    public static final String A_ERROR = "error";
    public static final String LOG_ERROR = "Error message: {}";
    public static final String SERVER_ERROR = "Server error";
    public static final String FAILED_ITEM_ID = "Failed Item id: %s";
    public static final String FAILED_USER_ID = "Failed user id: %s";
    public static final String FAILED_OWNER_ID = "Failed owner id: %s";
    public static final String FAILED_BOOKING_ID = "Failed booking id: %s";
    public static final String FAILED_REQUEST = "Failed request parameters";
    public static final String DUPLICATED_EMAIL = "Duplicated email found: %s";

    @ExceptionHandler({
            ValidationException.class,
            IllegalArgumentException.class,
            RequestNotValidException.class
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
    public ResponseEntity<Map<String, String>> validationEmailHandler(DataIntegrityViolationException error) {
        log.warn(LOG_ERROR, error.getMessage());
        return ResponseEntity.status(409).body(Map.of(A_ERROR, Objects.requireNonNull(error.getMessage())));
    }

    @ExceptionHandler
    public ResponseEntity<Map<String, String>> otherServerErrorsHandler(Throwable error) {
        log.warn(LOG_ERROR, error.getMessage());
        return ResponseEntity.status(500).body(Map.of(SERVER_ERROR, error.getMessage()));
    }

}
