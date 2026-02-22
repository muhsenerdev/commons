package github.muhsenerdev.plans.domain.subscription;

import java.time.LocalDate;
import java.util.UUID;

import github.muhsenerdev.plans.domain.shared.Interval;

import lombok.Builder;

@Builder
public record SubscriptionFilter(
        UUID userId,
        UUID planId,
        UUID priceId,
        SubscriptionStatus status,
        Interval interval,
        DateRange currentPeriodStart,
        DateRange createdAt,
        DateRange currentPeriodEnd) {

    public record DateRange(LocalDate start, LocalDate end) {
    }
}
