package com.safetywing.navigator.domain.model;

import java.time.LocalDate;

/**
 * Represents an official source document (policy PDF, product page, etc.)
 * that backs the information presented to users.
 */
public record SourceDocument(
        String id, String title, String url, SourceType type, LocalDate effectiveDate, LocalDate reviewedDate) {
    public SourceDocument {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("SourceDocument id cannot be null or blank");
        }
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("SourceDocument title cannot be null or blank");
        }
        if (url == null || url.isBlank()) {
            throw new IllegalArgumentException("SourceDocument url cannot be null or blank");
        }
        if (type == null) {
            throw new IllegalArgumentException("SourceDocument type cannot be null");
        }
        if (reviewedDate == null) {
            throw new IllegalArgumentException("SourceDocument reviewedDate cannot be null");
        }
    }

    public enum SourceType {
        POLICY_DOCUMENT,
        MARKETING_PAGE,
        HELP_CENTER,
        BLOG_POST
    }
}
