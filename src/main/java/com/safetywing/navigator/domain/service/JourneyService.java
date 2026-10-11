package com.safetywing.navigator.domain.service;

import com.safetywing.navigator.api.exception.ContentNotAvailableException;
import com.safetywing.navigator.api.exception.UnsupportedScenarioException;
import com.safetywing.navigator.domain.model.BenefitContent;
import com.safetywing.navigator.domain.model.NextStep;
import com.safetywing.navigator.domain.model.Scenario;
import com.safetywing.navigator.domain.model.SourceDocument;
import com.safetywing.navigator.domain.repository.ScenarioRepository;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * Service for processing user journey requests.
 * Validates scenario selection and retrieves complete benefit information.
 */
@Service
public class JourneyService {

    private final ScenarioRepository scenarioRepository;
    private final ScenarioService scenarioService;
    private final ContentService contentService;

    JourneyService(
            ScenarioRepository scenarioRepository, ScenarioService scenarioService, ContentService contentService) {
        this.scenarioRepository = scenarioRepository;
        this.scenarioService = scenarioService;
        this.contentService = contentService;
    }

    /**
     * Processes a journey request for a given scenario.
     * Returns complete journey result including benefit information and next steps.
     *
     * @param scenarioId the selected scenario ID
     * @return journey result with all information
     * @throws UnsupportedScenarioException if scenario is not supported
     * @throws ContentNotAvailableException if content is missing
     */
    public JourneyResult processJourney(String scenarioId) {
        // Validate scenario exists
        if (!scenarioService.scenarioExists(scenarioId)) {
            throw new UnsupportedScenarioException(scenarioId);
        }

        // Get scenario details
        Scenario scenario =
                scenarioRepository.findById(scenarioId).orElseThrow(() -> new UnsupportedScenarioException(scenarioId));

        // Get benefit content (this validates source integrity)
        BenefitContent content;
        try {
            content = contentService.getContentForScenario(scenarioId);
        } catch (IllegalStateException e) {
            throw new ContentNotAvailableException(scenarioId);
        }

        // Get source documents
        List<SourceDocument> sources = contentService.getSourcesForContent(content);

        // Build next steps
        NextStep primaryStep = new NextStep(
                "Contact SafetyWing Support",
                "https://safetywing.com/contact",
                "Live chat and email available for personalized guidance");

        NextStep secondaryStep = new NextStep(
                "Read Full Policy Terms",
                "https://safetywing.com/nomad-citizen/nomad-citizen-policy-v2-2026-05.pdf",
                "Download the complete policy certificate (PDF)");

        return new JourneyResult(scenario, content, sources, primaryStep, secondaryStep);
    }

    /**
     * Result object containing all information needed for the journey response.
     */
    public record JourneyResult(
            Scenario scenario,
            BenefitContent benefitContent,
            List<SourceDocument> sources,
            NextStep primaryNextStep,
            NextStep secondaryNextStep) {}
}
