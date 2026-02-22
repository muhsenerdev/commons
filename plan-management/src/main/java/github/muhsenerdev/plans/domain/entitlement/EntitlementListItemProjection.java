package github.muhsenerdev.plans.domain.entitlement;

import java.time.OffsetDateTime;
import java.util.UUID;

public interface EntitlementListItemProjection {
    UUID getId();

    UUID getUserId();

    UUID getSubscriptionId();

    String getFeatureCode();

    Integer getTotalAmount();

    Integer getUsedAmount();

    EntitlementSourceType getSourceType();

    OffsetDateTime getValidFrom();

    OffsetDateTime getValidTo();

    EntitlementStatus getStatus();

    default Integer getRemainingAmount() {
        int total = getTotalAmount() != null ? getTotalAmount() : 0;
        int used = getUsedAmount() != null ? getUsedAmount() : 0;
        return Math.max(0, total - used);
    }
}
