package github.muhsenerdev.plans.domain.subscription;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.UUID;

import github.muhsenerdev.commons.core.vo.Money;
import github.muhsenerdev.plans.domain.shared.Interval;

public interface SubscriptionListItemProjection {
    UUID getId();

    UUID getUserId();

    SubscriptionStatus getStatus();

    Interval getInterval();

    OffsetDateTime getCurrentPeriodStart();

    OffsetDateTime getCurrentPeriodEnd();

    Instant getCreatedAt();

    // Plan details
    String getPlanName();

    UUID getPlanId();

    // Price details
    // BigDecimal getPriceAmount();

    // String getPriceCurrency();

    Money getPricePrice();

    UUID getPriceId();
}
