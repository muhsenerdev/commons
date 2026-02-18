package github.muhsenerdev.plans.application.plan.shared;

import github.muhsenerdev.plans.domain.plan.PlanType;
import lombok.Builder;
import java.util.List;

@Builder(toBuilder = true)
public record PlanPaymentDto(
        String code,
        String name,
        String description,
        PlanType type,
        List<PricePaymentDto> prices,
        String providerId // Stripe Product ID
) {
}
