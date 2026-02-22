package github.muhsenerdev.plans.application.subscription.checkout;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import github.muhsenerdev.commons.core.exception.NotFoundException;
import github.muhsenerdev.plans.application.shared.EntitlementService;
import github.muhsenerdev.plans.domain.plan.Plan;
import github.muhsenerdev.plans.domain.plan.PlanRepository;
import github.muhsenerdev.plans.domain.shared.EntitlementUtil;
import github.muhsenerdev.plans.domain.subscription.Subscription;
import github.muhsenerdev.plans.domain.subscription.SubscriptionInfo;
import github.muhsenerdev.plans.domain.subscription.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubscriptionCycledHandler {

    private final SubscriptionRepository subscriptionRepository;
    private final PlanRepository planRepository;
    private final EntitlementService entitlementService;

    @Transactional
    public void handle(SubscriptionInfo info) {
        log.info("Handling subscription cycled for subscriptionId: {}", info.subscriptionId());

        Subscription subscription = subscriptionRepository.findById(info.subscriptionId())
                .orElseThrow(() -> new NotFoundException("Subscription not found: " + info.subscriptionId()));

        Plan plan = planRepository.findWithFeaturesDeeplyById(subscription.getPlanId())
                .orElseThrow(() -> new NotFoundException("Plan not found: " + subscription.getPlanId()));

        var entitlements = EntitlementUtil.extractEntitlements(plan.getFeatures());

        subscription.renew(info.periodStart(), info.periodEnd(), entitlements);
        entitlementService.grantEntitlements(subscription);

        log.info("Subscription {} renewed successfully for the next cycle.", info.subscriptionId());
    }
}
