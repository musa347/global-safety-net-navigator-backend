package com.safetywing.navigator.api.dto.request;

import jakarta.validation.constraints.NotBlank;

/**
 * Request DTO for journey submission.
 */
public record JourneyRequest(
        @NotBlank(message = "scenarioId cannot be null or blank")
        String scenarioId) {}
