package com.safetywing.navigator.domain.service;

import com.safetywing.navigator.domain.model.BenefitContent;
import com.safetywing.navigator.domain.repository.SourceRepository;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

/**
 * Service responsible for validating that all source references in content
 * have corresponding source documents. This prevents displaying content
 * that references non-existent sources.
 */
@Service
public class SourceValidationService {

    private final SourceRepository sourceRepository;

    SourceValidationService(SourceRepository sourceRepository) {
        this.sourceRepository = sourceRepository;
    }

    /**
     * Validates that all source IDs referenced in the benefit content
     * have matching source documents in the repository.
     *
     * @param content the benefit content to validate
     * @throws IllegalStateException if any source ID is missing
     */
    public void validateSourceIntegrity(BenefitContent content) {
        Set<String> referencedSourceIds = content.policyConditions().stream()
                .map(condition -> condition.sourceId())
                .collect(Collectors.toSet());

        List<String> missingSourceIds = referencedSourceIds.stream()
                .filter(sourceId -> !sourceRepository.existsById(sourceId))
                .toList();

        if (!missingSourceIds.isEmpty()) {
            throw new IllegalStateException(
                    "Content references non-existent sources: " + String.join(", ", missingSourceIds));
        }
    }
}
