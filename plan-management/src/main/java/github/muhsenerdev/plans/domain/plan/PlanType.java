package github.muhsenerdev.plans.domain.plan;

import java.util.Optional;

import github.muhsenerdev.commons.core.exception.InvalidInputException;

public enum PlanType {
    FREE,
    PAID;

    public static PlanType fromStringOrNull(String type) {
        for (PlanType planType : PlanType.values()) {
            if (planType.name().equals(type)) {
                return planType;
            }
        }
        return null;
    }

    public static PlanType fromString(String value) {
        return Optional.ofNullable(fromStringOrNull(value))
                .orElseThrow(() -> new InvalidInputException("plan_type", "Unknown plan type: {}", value));
    }
}
