package com.safetywing.navigator.api.controller;

import com.safetywing.navigator.api.dto.response.ScenarioResponse;
import com.safetywing.navigator.domain.model.Scenario;
import com.safetywing.navigator.domain.service.ScenarioService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for scenario catalogue operations.
 * Provides the list of supported income-disruption scenarios.
 */
@RestController
@RequestMapping("/api/v1/income/scenarios")
@CrossOrigin(origins = "http://localhost:3000")
class ScenarioController {

    private final ScenarioService scenarioService;

    ScenarioController(ScenarioService scenarioService) {
        this.scenarioService = scenarioService;
    }

    /**
     * GET /api/v1/income/scenarios
     * Returns the catalogue of supported scenarios.
     *
     * @return scenario catalogue response
     */
    @GetMapping
    public ResponseEntity<ScenarioResponse> getScenarios() {
        List<Scenario> scenarios = scenarioService.getAllScenarios();
        ScenarioResponse response = ScenarioResponse.from(scenarios);
        return ResponseEntity.ok(response);
    }
}
