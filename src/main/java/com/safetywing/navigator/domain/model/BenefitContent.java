package com.safetywing.navigator.domain.model;

import java.util.List;

/**
 * Represents the complete benefit information for a scenario,
 * including policy conditions, benefit structure, and verification items.
 */
public record BenefitContent(
        String scenarioId,
        String summary,
        List<PolicyCondition> policyConditions,
        BenefitStructure benefitStructure,
        List<String> itemsToVerify,
        String disclaimerText,
        DisclaimerImportance disclaimerImportance) {
    public BenefitContent {
        if (scenarioId == null || scenarioId.isBlank()) {
            throw new IllegalArgumentException("BenefitContent scenarioId cannot be null or blank");
        }
        if (summary == null || summary.isBlank()) {
            throw new IllegalArgumentException("BenefitContent summary cannot be null or blank");
        }
        if (policyConditions == null || policyConditions.isEmpty()) {
            throw new IllegalArgumentException("BenefitContent must have at least one policy condition");
        }
        if (benefitStructure == null) {
            throw new IllegalArgumentException("BenefitContent benefitStructure cannot be null");
        }
        if (itemsToVerify == null || itemsToVerify.isEmpty()) {
            throw new IllegalArgumentException("BenefitContent must have at least one item to verify");
        }
        if (disclaimerText == null || disclaimerText.isBlank()) {
            throw new IllegalArgumentException("BenefitContent disclaimerText cannot be null or blank");
        }
        if (disclaimerImportance == null) {
            throw new IllegalArgumentException("BenefitContent disclaimerImportance cannot be null");
        }
    }

    public enum DisclaimerImportance {
        CRITICAL,
        HIGH,
        MEDIUM
    }

    /**
     * Represents the benefit structure with monetary amounts and duration limits.
     */
    public record BenefitStructure(
            MonetaryBenefit maximumMonthlyBenefit, Duration maximumDuration, String frequencyLimit) {
        public BenefitStructure {
            if (maximumMonthlyBenefit == null) {
                throw new IllegalArgumentException("BenefitStructure maximumMonthlyBenefit cannot be null");
            }
            if (maximumDuration == null) {
                throw new IllegalArgumentException("BenefitStructure maximumDuration cannot be null");
            }
            if (frequencyLimit == null || frequencyLimit.isBlank()) {
                throw new IllegalArgumentException("BenefitStructure frequencyLimit cannot be null or blank");
            }
        }
    }

    /**
     * Represents a monetary benefit amount with currency and qualifier.
     */
    public record MonetaryBenefit(int amount, String currency, String qualifier) {
        public MonetaryBenefit {
            if (amount <= 0) {
                throw new IllegalArgumentException("MonetaryBenefit amount must be positive");
            }
            if (currency == null || currency.isBlank()) {
                throw new IllegalArgumentException("MonetaryBenefit currency cannot be null or blank");
            }
            if (qualifier == null || qualifier.isBlank()) {
                throw new IllegalArgumentException("MonetaryBenefit qualifier cannot be null or blank");
            }
        }
    }

    /**
     * Represents a duration with value, unit, and qualifier.
     */
    public record Duration(int value, String unit, String qualifier) {
        public Duration {
            if (value <= 0) {
                throw new IllegalArgumentException("Duration value must be positive");
            }
            if (unit == null || unit.isBlank()) {
                throw new IllegalArgumentException("Duration unit cannot be null or blank");
            }
            if (qualifier == null || qualifier.isBlank()) {
                throw new IllegalArgumentException("Duration qualifier cannot be null or blank");
            }
        }
    }
}
