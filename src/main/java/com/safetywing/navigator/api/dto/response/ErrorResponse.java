package com.safetywing.navigator.api.dto.response;

import java.time.Instant;

/**
 * Standard error response DTO.
 * Matches API specification from PRD Section 2.3.
 */
public record ErrorResponse(ErrorDto error) {
    public record ErrorDto(
            String code, String message, String field, String submittedValue, String requestId, Instant timestamp) {}

    public static ErrorResponse create(String code, String message, String requestId) {
        return new ErrorResponse(new ErrorDto(code, message, null, null, requestId, Instant.now()));
    }

    public static ErrorResponse createWithField(
            String code, String message, String field, String submittedValue, String requestId) {
        return new ErrorResponse(new ErrorDto(code, message, field, submittedValue, requestId, Instant.now()));
    }
}
