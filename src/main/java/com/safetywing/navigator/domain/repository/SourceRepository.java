package com.safetywing.navigator.domain.repository;

import com.safetywing.navigator.domain.model.SourceDocument;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/**
 * In-memory repository for official source documents.
 * Contains all verified sources from the Content Verification Document.
 */
@Repository
public class SourceRepository {

    private static final List<SourceDocument> SOURCES = List.of(
            new SourceDocument(
                    "SRC-001",
                    "Nomad Citizen Product Page",
                    "https://safetywing.com/nomad-citizen",
                    SourceDocument.SourceType.MARKETING_PAGE,
                    null,
                    LocalDate.of(2026, 10, 10)),
            new SourceDocument(
                    "SRC-002",
                    "Nomad Citizen FAQ (Help Center)",
                    "https://intercom.help/safetywing/en/articles/11088830-nomad-citizen-faq",
                    SourceDocument.SourceType.HELP_CENTER,
                    null,
                    LocalDate.of(2026, 10, 10)),
            new SourceDocument(
                    "SRC-003",
                    "Juan's Customer Story",
                    "https://safetywing.com/blog/nomad-citizen-juan/",
                    SourceDocument.SourceType.BLOG_POST,
                    null,
                    LocalDate.of(2026, 10, 10)),
            new SourceDocument(
                    "SRC-004",
                    "Nomad Citizen Policy Certificate",
                    "https://safetywing.com/nomad-citizen/nomad-citizen-policy-v2-2026-05.pdf",
                    SourceDocument.SourceType.POLICY_DOCUMENT,
                    LocalDate.of(2026, 7, 1),
                    LocalDate.of(2026, 10, 10)),
            new SourceDocument(
                    "SRC-005",
                    "SafetyWing Contact Page",
                    "https://safetywing.com/contact",
                    SourceDocument.SourceType.MARKETING_PAGE,
                    null,
                    LocalDate.of(2026, 10, 10)));

    public List<SourceDocument> findAll() {
        return SOURCES;
    }

    public Optional<SourceDocument> findById(String id) {
        return SOURCES.stream().filter(source -> source.id().equals(id)).findFirst();
    }

    public List<SourceDocument> findAllById(List<String> ids) {
        return SOURCES.stream().filter(source -> ids.contains(source.id())).toList();
    }

    public boolean existsById(String id) {
        return SOURCES.stream().anyMatch(source -> source.id().equals(id));
    }
}
