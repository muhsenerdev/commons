package github.muhsenerdev.plans.application.plan.shared;

import java.util.UUID;

import lombok.Builder;
import lombok.Getter;

@Builder(toBuilder = true)
@Getter
public class CheckoutInfo {
    private final String url;
    private final UUID subscriptionId;
}
