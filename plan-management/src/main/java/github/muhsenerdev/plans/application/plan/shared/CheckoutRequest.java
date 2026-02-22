package github.muhsenerdev.plans.application.plan.shared;

import java.util.UUID;

import lombok.Builder;

@Builder
public record CheckoutRequest(
        UUID userId,
        UUID subscriptionId,
        String planProviderId,
        String priceProviderId) {
}
