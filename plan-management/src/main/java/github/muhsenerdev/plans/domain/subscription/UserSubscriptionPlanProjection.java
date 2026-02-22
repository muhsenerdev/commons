package github.muhsenerdev.plans.domain.subscription;

import java.time.OffsetDateTime;
import java.util.UUID;

import github.muhsenerdev.plans.domain.shared.Interval;

public interface UserSubscriptionPlanProjection {

    UUID getSubscriptionId();

    Interval getSubscriptionInterval();

    OffsetDateTime getCurrentPeriodStart();

    OffsetDateTime getCurrentPeriodEnd();

    // Plan Details

    UUID getPlanId();

    String getPlanName();

    String getPlanCode();

    String getPlanType();
}
