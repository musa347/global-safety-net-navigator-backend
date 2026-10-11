package com.safetywing.navigator.domain.service;

import com.safetywing.navigator.domain.model.Scenario;
import com.safetywing.navigator.domain.repository.ScenarioRepository;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * Service for managing income-disruption scenarios.
 * Provides access to the catalogue of supported scenarios.
 */
@Service
public class ScenarioService {

    private final ScenarioRepository scenarioRepository;

    ScenarioService(ScenarioRepository scenarioRepository) {
        this.scenarioRepository = scenarioRepository;
    }

    /**
     * Retrieves all available scenarios.
     * Only returns scenarios with complete, verified content.
     *
     * @return list of all scenarios
     */
    public List<Scenario> getAllScenarios() {
        return scenarioRepository.findAll();
    }

    /**
     * Checks if a scenario exists by its ID.
     *
     * @param scenarioId the scenario ID to check
     * @return true if the scenario exists
     */
    public boolean scenarioExists(String scenarioId) {
        return scenarioRepository.existsById(scenarioId);
    }
}
