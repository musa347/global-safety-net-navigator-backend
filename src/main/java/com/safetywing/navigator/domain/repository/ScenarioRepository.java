package com.safetywing.navigator.domain.repository;

import com.safetywing.navigator.domain.model.Scenario;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/**
 * In-memory repository for income-disruption scenarios.
 * Contains only verified scenarios with complete content.
 */
@Repository
public class ScenarioRepository {

    private static final List<Scenario> SCENARIOS = List.of(
            new Scenario(
                    "CONTRACT_CANCELLED",
                    "My client cancelled a contract",
                    "Explore income-protection information for cancelled contracts and severe loss of variable income."),
            new Scenario(
                    "INCOME_DECREASED_SIGNIFICANTLY",
                    "My income decreased significantly",
                    "Explore income-protection information for significant income decreases due to client loss, project cancellations, or market changes."));

    public List<Scenario> findAll() {
        return SCENARIOS;
    }

    public Optional<Scenario> findById(String id) {
        return SCENARIOS.stream().filter(scenario -> scenario.id().equals(id)).findFirst();
    }

    public boolean existsById(String id) {
        return SCENARIOS.stream().anyMatch(scenario -> scenario.id().equals(id));
    }
}
