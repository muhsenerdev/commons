package github.muhsenerdev.plans.application.subscription.checkout;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import github.muhsenerdev.commons.core.exception.NotFoundException;
import github.muhsenerdev.plans.domain.subscription.Subscription;
import github.muhsenerdev.plans.domain.subscription.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class CyclePaymentFailedHandler {

    private final SubscriptionRepository subscriptionRepository;

    @Transactional
    public void handle(UUID subscriptionId) {
        log.info("Handling cycle payment failure for subscriptionId: {}", subscriptionId);

        Subscription subscription = subscriptionRepository.findById(subscriptionId)
                .orElseThrow(() -> new NotFoundException("Subscription not found: " + subscriptionId));

        subscription.grace();

        subscriptionRepository.save(subscription);

        log.info("Subscription {} marked as PAST_DUE due to payment failure.", subscriptionId);
    }
}
