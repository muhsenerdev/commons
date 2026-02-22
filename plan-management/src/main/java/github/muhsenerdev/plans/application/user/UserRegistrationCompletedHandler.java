package github.muhsenerdev.plans.application.user;

import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import github.muhsenerdev.plans.application.shared.EntitlementService;
import github.muhsenerdev.plans.domain.plan.Plan;
import github.muhsenerdev.plans.domain.plan.PlanRepository;
import github.muhsenerdev.plans.domain.subscription.Subscription;
import github.muhsenerdev.plans.domain.subscription.SubscriptionDomainService;
import github.muhsenerdev.plans.domain.subscription.SubscriptionInfo;
import github.muhsenerdev.plans.domain.subscription.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserRegistrationCompletedHandler {

    private final PlanRepository planRepository;
    private final SubscriptionDomainService subscriptionDomainService;
    private final SubscriptionRepository subscriptionRepository;
    private final EntitlementService entitlementService;

    @Transactional
    public void handle(UUID userId) {
        Plan freePlan = planRepository.findActiveFreePlan()
                .orElseThrow(() -> new IllegalStateException("Active FREE plan not found"));

        Subscription subscription = subscriptionDomainService.subscribe(userId, freePlan, null);
        subscriptionRepository.save(subscription);

        SubscriptionInfo info = SubscriptionInfo.builder()
                .subscriptionId(subscription.getId())
                .providerId("SYSTEM")
                .periodStart(Instant.now())
                .periodEnd(null)
                .build();
        subscription.activate(info);
        subscriptionRepository.save(subscription);
        entitlementService.grantEntitlements(subscription);

        log.info("Default FREE plan subscription completed for user: {}", userId);
    }
}
