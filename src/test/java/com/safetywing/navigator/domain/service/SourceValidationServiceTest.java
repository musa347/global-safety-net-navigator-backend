package com.safetywing.navigator.domain.service;

import static org.assertj.core.api.Assertions.*;

import com.safetywing.navigator.domain.model.BenefitContent;
import com.safetywing.navigator.domain.model.PolicyCondition;
import com.safetywing.navigator.domain.repository.SourceRepository;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Tests for source integrity validation.
 * Ensures the critical invariant: every policy condition must reference an existing source.
 */
@SpringBootTest
class SourceValidationServiceTest {

    @Autowired
    private SourceValidationService sourceValidationService;

    @Autowired
    private SourceRepository sourceRepository;

    @Test
    @DisplayName("Content with valid source references passes validation")
    void validSourceReferences_PassesValidation() {
        BenefitContent validContent = new BenefitContent(
                "TEST_SCENARIO",
                "Test summary",
                List.of(new PolicyCondition("PC1", "Test Condition", "Description", "SRC-004")),
                new BenefitContent.BenefitStructure(
                        new BenefitContent.MonetaryBenefit(1000, "USD", "up to"),
                        new BenefitContent.Duration(3, "months", "up to"),
                        "Test frequency"),
                List.of("Test item"),
                "Test disclaimer",
                BenefitContent.DisclaimerImportance.CRITICAL);

        // Should not throw exception
        assertThatCode(() -> sourceValidationService.validateSourceIntegrity(validContent))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("Content with invalid source reference fails validation")
    void invalidSourceReferences_FailsValidation() {
        BenefitContent invalidContent = new BenefitContent(
                "TEST_SCENARIO",
                "Test summary",
                List.of(new PolicyCondition("PC1", "Test Condition", "Description", "SRC-NONEXISTENT")),
                new BenefitContent.BenefitStructure(
                        new BenefitContent.MonetaryBenefit(1000, "USD", "up to"),
                        new BenefitContent.Duration(3, "months", "up to"),
                        "Test frequency"),
                List.of("Test item"),
                "Test disclaimer",
                BenefitContent.DisclaimerImportance.CRITICAL);

        assertThatThrownBy(() -> sourceValidationService.validateSourceIntegrity(invalidContent))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Content references non-existent sources")
                .hasMessageContaining("SRC-NONEXISTENT");
    }

    @Test
    @DisplayName("All sources referenced by real content exist in repository")
    void realContentSources_AllExistInRepository() {
        // This test validates that our actual curated content doesn't reference missing sources
        List<String> sourceIds = List.of("SRC-001", "SRC-004");

        for (String sourceId : sourceIds) {
            assertThat(sourceRepository.existsById(sourceId))
                    .as("Source %s should exist in repository", sourceId)
                    .isTrue();
        }
    }
}
