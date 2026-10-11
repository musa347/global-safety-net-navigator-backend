package com.safetywing.navigator.api.dto.response;

import com.safetywing.navigator.domain.model.*;
import com.safetywing.navigator.domain.service.JourneyService;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

/**
 * Response DTO for journey submission endpoint.
 * Matches API specification from PRD Section 2.2.
 */
public record JourneyResponse(
        JourneyDto journey,
        BenefitInformationDto benefitInformation,
        DisclaimerDto disclaimer,
        List<SourceDto> sources,
        NextStepsDto nextSteps,
        MetadataDto metadata) {
    public record JourneyDto(String scenarioId, String scenarioLabel, String resultType) {}

    public record BenefitInformationDto(
            String summary,
            List<PolicyConditionDto> policyConditions,
            BenefitStructureDto benefitStructure,
            List<String> itemsToVerify) {}

    public record PolicyConditionDto(String id, String title, String description, String sourceId) {
        public static PolicyConditionDto from(PolicyCondition condition) {
            return new PolicyConditionDto(
                    condition.id(), condition.title(), condition.description(), condition.sourceId());
        }
    }

    public record BenefitStructureDto(
            MonetaryBenefitDto maximumMonthlyBenefit, DurationDto maximumDuration, String frequencyLimit) {}

    public record MonetaryBenefitDto(int amount, String currency, String qualifier) {}

    public record DurationDto(int value, String unit, String qualifier) {}

    public record DisclaimerDto(String text, String importance) {}

    public record SourceDto(
            String id, String title, String url, String type, LocalDate effectiveDate, LocalDate reviewedDate) {
        public static SourceDto from(SourceDocument source) {
            return new SourceDto(
                    source.id(),
                    source.title(),
                    source.url(),
                    source.type().name(),
                    source.effectiveDate(),
                    source.reviewedDate());
        }
    }

    public record NextStepsDto(NextStepDto primary, NextStepDto secondary) {}

    public record NextStepDto(String label, String url, String description) {
        public static NextStepDto from(NextStep nextStep) {
            return new NextStepDto(nextStep.label(), nextStep.url(), nextStep.description());
        }
    }

    public record MetadataDto(String requestId, Instant timestamp, String contentVersion) {}

    public static JourneyResponse from(JourneyService.JourneyResult result, String requestId) {
        BenefitContent content = result.benefitContent();
        Scenario scenario = result.scenario();

        JourneyDto journeyDto = new JourneyDto(scenario.id(), scenario.label(), "INFORMATIONAL");

        BenefitInformationDto benefitInfo = new BenefitInformationDto(
                content.summary(),
                content.policyConditions().stream()
                        .map(PolicyConditionDto::from)
                        .toList(),
                new BenefitStructureDto(
                        new MonetaryBenefitDto(
                                content.benefitStructure()
                                        .maximumMonthlyBenefit()
                                        .amount(),
                                content.benefitStructure()
                                        .maximumMonthlyBenefit()
                                        .currency(),
                                content.benefitStructure()
                                        .maximumMonthlyBenefit()
                                        .qualifier()),
                        new DurationDto(
                                content.benefitStructure().maximumDuration().value(),
                                content.benefitStructure().maximumDuration().unit(),
                                content.benefitStructure().maximumDuration().qualifier()),
                        content.benefitStructure().frequencyLimit()),
                content.itemsToVerify());

        DisclaimerDto disclaimerDto = new DisclaimerDto(
                content.disclaimerText(), content.disclaimerImportance().name());

        List<SourceDto> sourceDtos =
                result.sources().stream().map(SourceDto::from).toList();

        NextStepsDto nextStepsDto = new NextStepsDto(
                NextStepDto.from(result.primaryNextStep()), NextStepDto.from(result.secondaryNextStep()));

        MetadataDto metadataDto = new MetadataDto(requestId, Instant.now(), "2026-07-01");

        return new JourneyResponse(journeyDto, benefitInfo, disclaimerDto, sourceDtos, nextStepsDto, metadataDto);
    }
}
