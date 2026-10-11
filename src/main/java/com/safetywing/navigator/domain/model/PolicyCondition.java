package com.safetywing.navigator.domain.model;

/**
 * Represents a single policy condition from the official policy document.
 * Each condition references a source document for attribution.
 */
public record PolicyCondition(String id, String title, String description, String sourceId) {
    public PolicyCondition {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("PolicyCondition id cannot be null or blank");
        }
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("PolicyCondition title cannot be null or blank");
        }
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("PolicyCondition description cannot be null or blank");
        }
        if (sourceId == null || sourceId.isBlank()) {
            throw new IllegalArgumentException("PolicyCondition sourceId cannot be null or blank");
        }
    }
}
