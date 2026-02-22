package github.muhsenerdev.plans.application.shared;

import github.muhsenerdev.plans.domain.subscription.Subscription;
import github.muhsenerdev.plans.domain.subscription.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class SubscriptionMaintenanceProcessor {

    private final SubscriptionRepository subscriptionRepository;
    private final SubscriptionAutoMaintenanceService subscriptionAutoMaintenanceService;

    public void process() {
        OffsetDateTime now = OffsetDateTime.now(ZoneOffset.UTC);
        List<Subscription> subscriptions = subscriptionRepository
                .findAllByAutoMaintenanceTrueAndNextMaintenanceDateBefore(now);

        if (subscriptions.isEmpty()) {
            return;
        }

        log.info("Found {} subscriptions for auto maintenance", subscriptions.size());

        for (Subscription subscription : subscriptions) {
            try {
                subscriptionAutoMaintenanceService.doAutoMaintenance(subscription);
                subscriptionRepository.save(subscription);
                log.info("Auto maintenance completed for subscription: {}", subscription.getId());
            } catch (Exception e) {
                log.error("Error processing auto maintenance for subscription {}: {}", subscription.getId(),
                        e.getMessage());
            }
        }
    }
}
