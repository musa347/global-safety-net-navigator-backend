package com.safetywing.navigator.domain.service;

import com.safetywing.navigator.domain.model.BenefitContent;
import com.safetywing.navigator.domain.model.SourceDocument;
import com.safetywing.navigator.domain.repository.ContentRepository;
import com.safetywing.navigator.domain.repository.SourceRepository;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

/**
 * Service for retrieving benefit content and source documents.
 * Validates source integrity before returning content.
 */
@Service
public class ContentService {

    private final ContentRepository contentRepository;
    private final SourceRepository sourceRepository;
    private final SourceValidationService sourceValidationService;

    ContentService(
            ContentRepository contentRepository,
            SourceRepository sourceRepository,
            SourceValidationService sourceValidationService) {
        this.contentRepository = contentRepository;
        this.sourceRepository = sourceRepository;
        this.sourceValidationService = sourceValidationService;
    }

    /**
     * Retrieves benefit content for a scenario and validates source integrity.
     *
     * @param scenarioId the scenario ID
     * @return benefit content
     * @throws IllegalStateException if content is missing or has invalid source references
     */
    public BenefitContent getContentForScenario(String scenarioId) {
        BenefitContent content = contentRepository
                .findByScenarioId(scenarioId)
                .orElseThrow(() -> new IllegalStateException("Content not available for scenario: " + scenarioId));

        // Validate that all referenced sources exist
        sourceValidationService.validateSourceIntegrity(content);

        return content;
    }

    /**
     * Retrieves all source documents referenced in the benefit content.
     * Always includes the Product Page (SRC-001) in addition to policy-referenced sources.
     *
     * @param content the benefit content
     * @return list of source documents
     */
    public List<SourceDocument> getSourcesForContent(BenefitContent content) {
        Set<String> sourceIds = content.policyConditions().stream()
                .map(condition -> condition.sourceId())
                .collect(Collectors.toSet());

        // Always include the Product Information page as a general source
        sourceIds.add("SRC-001");

        return sourceRepository.findAllById(List.copyOf(sourceIds));
    }
}
