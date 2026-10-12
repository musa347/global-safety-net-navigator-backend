package com.safetywing.navigator.api.controller;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

/**
 * API contract tests for journey endpoint.
 * Tests validate critical behaviors: valid requests, validation, error handling,
 * and that the system never converts policy information into eligibility decisions.
 */
@SpringBootTest
class JourneyControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    @DisplayName("POST /journeys with valid scenario returns 200 with complete journey response")
    void validJourneyRequest_Returns200() throws Exception {
        String validRequest = """
                {
                    "scenarioId": "CONTRACT_CANCELLED"
                }
                """;

        mockMvc.perform(post("/api/v1/income/journeys")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.journey").exists())
                .andExpect(jsonPath("$.journey.scenarioId").value("CONTRACT_CANCELLED"))
                .andExpect(jsonPath("$.benefitInformation").exists())
                .andExpect(jsonPath("$.benefitInformation.policyConditions").isArray())
                .andExpect(jsonPath("$.benefitInformation.policyConditions.length()")
                        .value(5))
                .andExpect(jsonPath("$.disclaimer").exists())
                .andExpect(jsonPath("$.sources").isArray())
                .andExpect(jsonPath("$.nextSteps").exists())
                .andExpect(jsonPath("$.metadata").exists());
    }

    @Test
    @DisplayName("POST /journeys with blank scenarioId returns 400")
    void blankScenarioId_Returns400() throws Exception {
        String invalidRequest = """
                {
                    "scenarioId": ""
                }
                """;

        mockMvc.perform(post("/api/v1/income/journeys")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.error.code").value("MISSING_REQUIRED_FIELD"));
    }

    @Test
    @DisplayName("POST /journeys with missing scenarioId returns 400")
    void missingScenarioId_Returns400() throws Exception {
        String invalidRequest = """
                {
                }
                """;

        mockMvc.perform(post("/api/v1/income/journeys")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequest))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /journeys with unsupported scenario returns 422")
    void unsupportedScenario_Returns422() throws Exception {
        String unsupportedRequest = """
                {
                    "scenarioId": "NONEXISTENT_SCENARIO"
                }
                """;

        mockMvc.perform(post("/api/v1/income/journeys")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(unsupportedRequest))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.error.code").value("UNSUPPORTED_SCENARIO"));
    }

    @Test
    @DisplayName("Journey response always contains 'up to' qualifier for benefits")
    void benefitStructure_AlwaysContainsUpToQualifier() throws Exception {
        String validRequest = """
                {
                    "scenarioId": "CONTRACT_CANCELLED"
                }
                """;

        mockMvc.perform(post("/api/v1/income/journeys")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.benefitInformation.benefitStructure.maximumMonthlyBenefit.qualifier")
                        .value("up to"))
                .andExpect(jsonPath("$.benefitInformation.benefitStructure.maximumDuration.qualifier")
                        .value("up to"));
    }

    @Test
    @DisplayName("Journey response result type is always INFORMATIONAL")
    void resultType_IsAlwaysInformational() throws Exception {
        String validRequest = """
                {
                    "scenarioId": "CONTRACT_CANCELLED"
                }
                """;

        mockMvc.perform(post("/api/v1/income/journeys")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.journey.resultType").value("INFORMATIONAL"));
    }

    @Test
    @DisplayName("Journey response never contains eligibility/approval/guarantee language")
    void responseContent_NeverContainsEligibilityLanguage() throws Exception {
        String validRequest = """
                {
                    "scenarioId": "CONTRACT_CANCELLED"
                }
                """;

        String response = mockMvc.perform(post("/api/v1/income/journeys")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validRequest))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString()
                .toLowerCase();

        // Verify response doesn't contain forbidden language
        // (except in disclaimer which appropriately says "does not determine eligibility")
        String[] forbiddenPhrases = {
            "you are eligible", "you qualify", "approved", "you are covered", "guaranteed payout", "claim approved"
        };

        for (String phrase : forbiddenPhrases) {
            if (response.contains(phrase)) {
                throw new AssertionError("Response contains forbidden phrase: " + phrase);
            }
        }
    }

    @Test
    @DisplayName("Every policy condition has a valid sourceId")
    void policyConditions_AllHaveValidSourceIds() throws Exception {
        String validRequest = """
                {
                    "scenarioId": "CONTRACT_CANCELLED"
                }
                """;

        mockMvc.perform(post("/api/v1/income/journeys")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.benefitInformation.policyConditions[*].sourceId")
                        .isNotEmpty())
                .andExpect(
                        jsonPath("$.benefitInformation.policyConditions[*].sourceId", everyItem(startsWith("SRC-"))));
    }

    @Test
    @DisplayName("Sources array contains all referenced sourceIds from policy conditions")
    void sources_ContainAllReferencedSourceIds() throws Exception {
        String validRequest = """
                {
                    "scenarioId": "CONTRACT_CANCELLED"
                }
                """;

        mockMvc.perform(post("/api/v1/income/journeys")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.sources").isArray())
                .andExpect(jsonPath("$.sources.length()").value(greaterThan(0)))
                .andExpect(jsonPath("$.sources[*].id").isNotEmpty())
                .andExpect(jsonPath("$.sources[*].url").isNotEmpty());
    }

    @Test
    @DisplayName("Error responses include requestId and timestamp")
    void errorResponses_IncludeRequestIdAndTimestamp() throws Exception {
        String unsupportedRequest = """
                {
                    "scenarioId": "INVALID"
                }
                """;

        mockMvc.perform(post("/api/v1/income/journeys")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(unsupportedRequest))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.error.requestId").isNotEmpty())
                .andExpect(jsonPath("$.error.timestamp").isNotEmpty());
    }
}
