package github.muhsenerdev.plans.domain.subscription;

import java.util.UUID;

import github.muhsenerdev.commons.core.exception.DomainException;

public class SubscriptionDomainException extends DomainException {

    public SubscriptionDomainException(String code, String message, Object... args) {
        super(code, message, args);
    }

    public static SubscriptionDomainException planNotActive(UUID planId) {
        return new SubscriptionDomainException("plan.not-active",
                "To subscribe to a plan, it must be active. PlanId: {}",
                planId);
    }

    public static SubscriptionDomainException subscriptionNotPending(UUID subscriptionId) {
        return new SubscriptionDomainException("subscription.not-pending",
                "To actiate a subscription it must be in PENDING state. SubscripitonId: {}", subscriptionId);
    }

    public static SubscriptionDomainException priceNotExists(UUID priceId, UUID planId) {
        return new SubscriptionDomainException("subcription.price_not_exist",
                "To subscribe to a plan, the price must exist. PriceId: {} in Plan: {}", priceId, planId);
    }

    public static SubscriptionDomainException priceNotActive(UUID priceId) {
        return new SubscriptionDomainException("subcription.price_not_active",
                "To subscribe to a plan, the price must be active. PriceId: {}", priceId);
    }

}
