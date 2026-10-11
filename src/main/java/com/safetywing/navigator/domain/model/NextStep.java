package com.safetywing.navigator.domain.model;

/**
 * Represents a recommended next step (action) for the user.
 */
public record NextStep(String label, String url, String description) {
    public NextStep {
        if (label == null || label.isBlank()) {
            throw new IllegalArgumentException("NextStep label cannot be null or blank");
        }
        if (url == null || url.isBlank()) {
            throw new IllegalArgumentException("NextStep url cannot be null or blank");
        }
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("NextStep description cannot be null or blank");
        }
    }
}
