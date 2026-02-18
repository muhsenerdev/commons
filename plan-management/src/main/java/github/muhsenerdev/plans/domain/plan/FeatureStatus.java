package github.muhsenerdev.plans.domain.plan;

import github.muhsenerdev.commons.core.exception.InvalidInputException;

import java.util.Optional;

public enum FeatureStatus {
    ACTIVE,
    INACTIVE;

    public static FeatureStatus fromString(String status) {
        return Optional.ofNullable(fromStringOrNull(status))
                .orElseThrow(() -> new InvalidInputException("Invalid feature status: " + status));
    }

    public static FeatureStatus fromStringOrNull(String status) {
        for (FeatureStatus featureStatus : values()) {
            if (featureStatus.name().equalsIgnoreCase(status)) {
                return featureStatus;
            }
        }
        return null;
    }
}
