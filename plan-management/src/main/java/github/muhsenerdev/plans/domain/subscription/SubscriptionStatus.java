package github.muhsenerdev.plans.domain.subscription;

public enum SubscriptionStatus {
    PENDING,
    ACTIVE,
    PAST_DUE,
    CANCELLED,
    CANCEL_AT_PERIOD_END,
    EXPIRED,
    CHECKOUT_EXPIRED,
    GRACE;
}
