package com.safetywing.navigator.api.exception;

/**
 * Exception thrown when a user requests a scenario that is not supported
 * or does not have verified content available.
 */
public class UnsupportedScenarioException extends RuntimeException {

    private final String scenarioId;

    public UnsupportedScenarioException(String scenarioId) {
        super("Unsupported scenario: " + scenarioId);
        this.scenarioId = scenarioId;
    }

    public String getScenarioId() {
        return scenarioId;
    }
}
