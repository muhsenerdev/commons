package github.muhsenerdev.plans.application.subscription.checkout;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import github.muhsenerdev.commons.core.exception.BusinessException;
import github.muhsenerdev.commons.core.exception.DuplicateException;
import github.muhsenerdev.commons.core.exception.NotFoundException;
import github.muhsenerdev.plans.application.plan.shared.CheckoutInfo;
import github.muhsenerdev.plans.application.plan.shared.CheckoutRequest;
import github.muhsenerdev.plans.application.plan.shared.PaymentGateway;
import github.muhsenerdev.plans.domain.plan.Plan;
import github.muhsenerdev.plans.domain.plan.PlanPrice;
import github.muhsenerdev.plans.domain.plan.PlanRepository;
import github.muhsenerdev.plans.domain.subscription.Subscription;
import github.muhsenerdev.plans.domain.subscription.SubscriptionDomainService;
import github.muhsenerdev.plans.domain.subscription.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class StartCheckoutCommandHandler {

    private final PlanRepository planRepository;
    private final SubscriptionDomainService subscriptionDomainService;
    private final SubscriptionRepository subscriptionRepository;
    private final PaymentGateway paymentGateway;

    @Transactional
    public CheckoutStartResponse handle(StartCheckoutCommand command) {
        ensureUserHasNoActiveSubscription(command.getUserId());

        Plan plan = planRepository.findWithFeaturesDeeplyById(command.getPlanId())
                .orElseThrow(() -> new NotFoundException("Plan not found: " + command.getPlanId()));

        ensurePlanIsNotFree(plan);

        var price = findPriceInPlanOrThrow(command, plan);

        Subscription subscription = createSubscription(command, plan);
        subscriptionRepository.save(subscription);

        CheckoutInfo checkoutInfo = startCheckout(command, plan, price, subscription);

        log.debug("Checkout started for user {} on subscription {}", command.getUserId(), subscription.getId());
        return new CheckoutStartResponse(checkoutInfo.getUrl(), subscription.getId());
    }

    private void ensureUserHasNoActiveSubscription(UUID userId) {
        if (subscriptionRepository.existsActiveSubscriptionOtherThanDefault(userId)) {
            throw new DuplicateException("subscription.duplicate",
                    "User already has an active or pending subscription.");
        }
    }

    private CheckoutInfo startCheckout(StartCheckoutCommand command, Plan plan, PlanPrice price,
            Subscription subscription) {
        CheckoutRequest request = CheckoutRequest.builder()
                .userId(command.getUserId())
                .subscriptionId(subscription.getId())
                .planProviderId(plan.getStripeProductId())
                .priceProviderId(price.getStripePriceId())
                .build();

        CheckoutInfo checkoutInfo = paymentGateway.startCheckout(request);
        return checkoutInfo;
    }

    private Subscription createSubscription(StartCheckoutCommand command, Plan plan) {
        return subscriptionDomainService.subscribe(command.getUserId(), plan,
                command.getPriceId());
    }

    private PlanPrice findPriceInPlanOrThrow(StartCheckoutCommand command, Plan plan) {
        return plan.findPrice(command.getPriceId())
                .orElseThrow(() -> new NotFoundException("Price not found: " + command.getPriceId()));
    }

    private void ensurePlanIsNotFree(Plan plan) {
        if (plan.isFree()) {
            throw new BusinessException("Checkout cannot be started for FREE plan.");
        }
    }
}
