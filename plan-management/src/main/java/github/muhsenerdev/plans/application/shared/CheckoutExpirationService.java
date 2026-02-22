package github.muhsenerdev.plans.application.shared;

import github.muhsenerdev.plans.domain.subscription.Subscription;
import github.muhsenerdev.plans.domain.subscription.SubscriptionRepository;
import github.muhsenerdev.plans.domain.subscription.SubscriptionStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CheckoutExpirationService {

    private final SubscriptionRepository subscriptionRepository;

    @Transactional
    public void expirePendingCheckouts() {
        Instant oneHourAgo = Instant.now().minus(1, ChronoUnit.HOURS);
        List<Subscription> expiredSubscriptions = subscriptionRepository.findAllByStatusAndCreatedAtBefore(
                SubscriptionStatus.PENDING,
                oneHourAgo);

        if (expiredSubscriptions.isEmpty()) {
            return;
        }

        log.info("Found {} periodic checkout records to expire", expiredSubscriptions.size());

        for (Subscription subscription : expiredSubscriptions) {
            try {
                subscription.expireCheckout();
                subscriptionRepository.save(subscription);
                log.info("Subscription checkout expired: {}", subscription.getId());
            } catch (Exception e) {
                log.error("Failed to expire checkout for subscription {}: {}", subscription.getId(), e.getMessage());
            }
        }
    }
}
