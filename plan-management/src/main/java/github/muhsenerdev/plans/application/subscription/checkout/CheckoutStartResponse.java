package github.muhsenerdev.plans.application.subscription.checkout;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.util.UUID;

@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public record CheckoutStartResponse(
        String checkoutUrl,
        UUID subscriptionId) {
}
