package com.safetywing.navigator.api.dto.response;

import com.safetywing.navigator.domain.model.Scenario;
import java.time.Instant;
import java.util.List;

/**
 * Response DTO for scenario catalogue endpoint.
 * Matches API specification from PRD Section 2.1.
 */
public record ScenarioResponse(List<ScenarioDto> scenarios, MetadataDto metadata) {
    /**
     * Content review timestamp - represents when the policy content was last reviewed,
     * not the time of the API request.
     */
    private static final Instant CONTENT_REVIEW_DATE = Instant.parse("2026-10-10T00:00:00Z");

    public record ScenarioDto(String id, String label, String description) {
        public static ScenarioDto from(Scenario scenario) {
            return new ScenarioDto(scenario.id(), scenario.label(), scenario.description());
        }
    }

    public record MetadataDto(int totalCount, Instant lastUpdated) {}

    public static ScenarioResponse from(List<Scenario> scenarios) {
        List<ScenarioDto> scenarioDtos =
                scenarios.stream().map(ScenarioDto::from).toList();

        return new ScenarioResponse(scenarioDtos, new MetadataDto(scenarioDtos.size(), CONTENT_REVIEW_DATE));
    }
}
