package com.safetywing.navigator.api.exception;

import com.safetywing.navigator.api.dto.response.ErrorResponse;
import com.safetywing.navigator.infrastructure.util.RequestIdGenerator;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Global exception handler for REST API.
 * Provides consistent error responses across all endpoints.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private final RequestIdGenerator requestIdGenerator;

    public GlobalExceptionHandler(RequestIdGenerator requestIdGenerator) {
        this.requestIdGenerator = requestIdGenerator;
    }

    /**
     * Handles validation errors from @Valid annotation.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        String requestId = requestIdGenerator.generate();

        String field = ex.getBindingResult().getFieldError() != null
                ? ex.getBindingResult().getFieldError().getField()
                : "unknown";

        String message = ex.getBindingResult().getFieldError() != null
                ? ex.getBindingResult().getFieldError().getDefaultMessage()
                : "Validation failed";

        log.warn("Validation error [requestId={}, field={}, message={}]", requestId, field, message);

        ErrorResponse response =
                ErrorResponse.createWithField("MISSING_REQUIRED_FIELD", message, field, null, requestId);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Handles constraint violation exceptions.
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex) {
        String requestId = requestIdGenerator.generate();

        log.warn("Constraint violation [requestId={}, message={}]", requestId, ex.getMessage());

        ErrorResponse response = ErrorResponse.create("INVALID_REQUEST_FORMAT", ex.getMessage(), requestId);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Handles unsupported scenario errors.
     */
    @ExceptionHandler(UnsupportedScenarioException.class)
    public ResponseEntity<ErrorResponse> handleUnsupportedScenario(UnsupportedScenarioException ex) {
        String requestId = requestIdGenerator.generate();

        log.warn("Unsupported scenario [requestId={}, scenarioId={}]", requestId, ex.getScenarioId());

        ErrorResponse response = ErrorResponse.createWithField(
                "UNSUPPORTED_SCENARIO", ex.getMessage(), "scenarioId", ex.getScenarioId(), requestId);

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(response);
    }

    /**
     * Handles content not available errors.
     */
    @ExceptionHandler(ContentNotAvailableException.class)
    public ResponseEntity<ErrorResponse> handleContentNotAvailable(ContentNotAvailableException ex) {
        String requestId = requestIdGenerator.generate();

        log.error("Content not available [requestId={}, scenarioId={}]", requestId, ex.getScenarioId());

        ErrorResponse response = ErrorResponse.create("CONTENT_NOT_AVAILABLE", ex.getMessage(), requestId);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    /**
     * Handles all other unexpected exceptions.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        String requestId = requestIdGenerator.generate();

        log.error("Unexpected error [requestId={}]", requestId, ex);

        ErrorResponse response = ErrorResponse.create(
                "INTERNAL_ERROR",
                "An unexpected error occurred. Please contact support with requestId: " + requestId,
                requestId);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
