package github.muhsenerdev.plans.domain.subscription;

import java.time.Instant;
import java.util.UUID;

import lombok.Builder;

@Builder
public record SubscriptionInfo(
        UUID subscriptionId,
        String providerId,
        Instant periodStart,
        Instant periodEnd,
        String cancellationReason) {

}
