package com.safetywing.navigator.api.exception;

/**
 * Exception thrown when benefit content is not available for a scenario.
 */
public class ContentNotAvailableException extends RuntimeException {

    private final String scenarioId;

    public ContentNotAvailableException(String scenarioId) {
        super("Content not available for scenario: " + scenarioId);
        this.scenarioId = scenarioId;
    }

    public String getScenarioId() {
        return scenarioId;
    }
}
