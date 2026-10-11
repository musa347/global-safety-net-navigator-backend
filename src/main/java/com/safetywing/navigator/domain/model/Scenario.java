package com.safetywing.navigator.domain.model;

/**
 * Represents an income-disruption scenario that users can explore.
 * Immutable domain model for a scenario in the income protection navigator.
 */
public record Scenario(String id, String label, String description) {
    public Scenario {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("Scenario id cannot be null or blank");
        }
        if (label == null || label.isBlank()) {
            throw new IllegalArgumentException("Scenario label cannot be null or blank");
        }
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Scenario description cannot be null or blank");
        }
    }
}
