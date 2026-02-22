package github.muhsenerdev.plans.application.subscription.checkout;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import github.muhsenerdev.commons.core.exception.NotFoundException;
import github.muhsenerdev.plans.application.shared.EntitlementService;
import github.muhsenerdev.plans.domain.subscription.Subscription;
import github.muhsenerdev.plans.domain.subscription.SubscriptionInfo;
import github.muhsenerdev.plans.domain.subscription.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class SubscriptionStartedHandler {

    private final SubscriptionRepository subscriptionRepository;
    private final EntitlementService entitlementService;

    @Transactional
    public void handle(SubscriptionInfo info) {
        log.info("Handling subscription started for subscriptionId: {}", info.subscriptionId());

        Subscription subscription = fetchSubscriptionOrThrow(info);

        if (subscription.activate(info)) {
            subscriptionRepository.save(subscription);
            entitlementService.grantEntitlements(subscription);
        }

        log.info("Subscription {} activated successfully via webhook.", info.subscriptionId());
    }

    private Subscription fetchSubscriptionOrThrow(SubscriptionInfo info) {
        return subscriptionRepository.findById(info.subscriptionId())
                .orElseThrow(() -> new NotFoundException("Subscription not found for ID: " + info.subscriptionId()));
    }
}
