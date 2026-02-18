package github.muhsenerdev.plans.domain.plan;

import java.time.Instant;
import java.util.UUID;

public interface PlanListProjection {
    UUID getId();

    String getCode();

    String getDescription();

    String getTitle();

    String getName();

    PlanType getType();

    PlanStatus getStatus();

    Instant getCreatedAt();

    Instant getUpdatedAt();

    Long getVersion();

    int getTier();
}
