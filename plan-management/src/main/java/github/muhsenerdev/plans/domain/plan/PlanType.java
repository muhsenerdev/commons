package github.muhsenerdev.plans.domain.plan;

import java.util.Optional;

import github.muhsenerdev.commons.core.exception.InvalidInputException;
import lombok.Builder;

public enum PlanType {
    FREE,
    PAID;

    @Builder
    public static PlanType fromStringOrNull(String value) {
        for (PlanType planType : PlanType.values()) {
            if (planType.name().equals(value)) {
                return planType;
            }
        }
        return null;
    }

    public static PlanType fromString(String value) {
        return Optional.ofNullable(fromStringOrNull(value))
                .orElseThrow(() -> new InvalidInputException("Invalid plan type: " + value));
    }
}
