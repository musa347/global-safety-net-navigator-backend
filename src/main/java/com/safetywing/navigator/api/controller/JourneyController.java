package com.safetywing.navigator.api.controller;

import com.safetywing.navigator.api.dto.request.JourneyRequest;
import com.safetywing.navigator.api.dto.response.JourneyResponse;
import com.safetywing.navigator.domain.service.JourneyService;
import com.safetywing.navigator.infrastructure.util.RequestIdGenerator;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for journey submission operations.
 * Processes user's scenario selection and returns benefit information.
 */
@RestController
@RequestMapping("/api/v1/income/journeys")
@CrossOrigin(origins = {"http://localhost:3000", "https://safetynetnavigator.vercel.app"})
class JourneyController {

    private static final Logger log = LoggerFactory.getLogger(JourneyController.class);

    private final JourneyService journeyService;
    private final RequestIdGenerator requestIdGenerator;

    JourneyController(JourneyService journeyService, RequestIdGenerator requestIdGenerator) {
        this.journeyService = journeyService;
        this.requestIdGenerator = requestIdGenerator;
    }

    /**
     * POST /api/v1/income/journeys
     * Processes a journey request for a selected scenario.
     *
     * @param request the journey request containing scenario ID
     * @return journey response with benefit information
     */
    @PostMapping
    public ResponseEntity<JourneyResponse> submitJourney(@Valid @RequestBody JourneyRequest request) {
        String requestId = requestIdGenerator.generate();

        log.info("Processing journey request [requestId={}, scenarioId={}]", requestId, request.scenarioId());

        JourneyService.JourneyResult result = journeyService.processJourney(request.scenarioId());
        JourneyResponse response = JourneyResponse.from(result, requestId);

        log.info("Journey request processed successfully [requestId={}]", requestId);

        return ResponseEntity.ok(response);
    }
}
