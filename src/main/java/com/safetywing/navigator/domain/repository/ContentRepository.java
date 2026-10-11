package com.safetywing.navigator.domain.repository;

import com.safetywing.navigator.domain.model.BenefitContent;
import com.safetywing.navigator.domain.model.PolicyCondition;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/**
 * In-memory repository for benefit content.
 * Contains complete verified content for each supported scenario.
 */
@Repository
public class ContentRepository {

    private static final Map<String, BenefitContent> CONTENT =
            Map.of("CONTRACT_CANCELLED", createContractCancelledContent());

    public Optional<BenefitContent> findByScenarioId(String scenarioId) {
        return Optional.ofNullable(CONTENT.get(scenarioId));
    }

    private static BenefitContent createContractCancelledContent() {
        return new BenefitContent(
                "CONTRACT_CANCELLED",
                "Nomad Citizen's income protection benefit covers severe loss of variable income when a client terminates a contract. The policy specifies conditions around income reduction thresholds, coverage duration, and documentation requirements.",
                createPolicyConditions(),
                createBenefitStructure(),
                createItemsToVerify(),
                "This information explains publicly available policy conditions. It does not determine your personal eligibility, approve a claim, or guarantee a payout. Any stated maximum is subject to meeting all policy conditions and requirements. Only SafetyWing can make authoritative eligibility and claim decisions.",
                BenefitContent.DisclaimerImportance.CRITICAL);
    }

    private static List<PolicyCondition> createPolicyConditions() {
        return List.of(
                new PolicyCondition(
                        "VC_INCOME_REDUCTION",
                        "Income Reduction Requirement",
                        "Income must have decreased by 90% or more, averaged over 90 consecutive days, due to termination or inability to agree upon a new contract.",
                        "SRC-004"),
                new PolicyCondition(
                        "VC_INCOME_HISTORY",
                        "Income History Requirement",
                        "The average income for the last year must be lower than the year before that.",
                        "SRC-004"),
                new PolicyCondition(
                        "VC_JOB_SEEKING",
                        "Job-Seeking Requirement",
                        "You must be actively seeking jobs or income matching your skills and education.",
                        "SRC-004"),
                new PolicyCondition(
                        "VC_PRIOR_COVERAGE",
                        "Prior Coverage Requirement",
                        "You must have been covered by the plan for at least 24 consecutive months before losing income.",
                        "SRC-004"),
                new PolicyCondition(
                        "VC_EVIDENCE",
                        "Evidence and Documentation",
                        "The policy lists documents that may be needed, including tax returns for the year of lost income (once available), documentation of income loss, and documentation of previous income.",
                        "SRC-004"));
    }

    private static BenefitContent.BenefitStructure createBenefitStructure() {
        return new BenefitContent.BenefitStructure(
                new BenefitContent.MonetaryBenefit(4000, "USD", "up to"),
                new BenefitContent.Duration(3, "months", "up to"),
                "Accessible up to 3 months every 24 months");
    }

    private static List<String> createItemsToVerify() {
        return List.of(
                "How the policy defines and applies its variable-income conditions to your situation",
                "Whether your income reduction meets the 90% threshold using the policy's calculation method",
                "Whether your coverage history meets the 24-month requirement",
                "What documentation is required for your specific situation",
                "Whether any exclusions or limitations apply to your circumstances",
                "What additional information SafetyWing may require from you");
    }
}
