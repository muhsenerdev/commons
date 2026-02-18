package github.muhsenerdev.plans.application.plan.shared;

import github.muhsenerdev.plans.domain.shared.Interval;
import lombok.Builder;
import java.math.BigDecimal;
import java.util.UUID;

@Builder(toBuilder = true)
public record PricePaymentDto(
        UUID id,
        BigDecimal amount,
        String currency,
        Interval interval,
        String providerId // Stripe Price ID
) {
}
