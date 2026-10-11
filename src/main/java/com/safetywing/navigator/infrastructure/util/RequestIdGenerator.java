package com.safetywing.navigator.infrastructure.util;

import java.util.UUID;
import org.springframework.stereotype.Component;

/**
 * Utility for generating unique request IDs for tracking and debugging.
 */
@Component
public class RequestIdGenerator {

    private static final String PREFIX = "req_";

    public String generate() {
        return PREFIX + UUID.randomUUID().toString().replace("-", "");
    }
}
