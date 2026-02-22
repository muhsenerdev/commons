package github.muhsenerdev.plans.application.shared;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import github.muhsenerdev.plans.domain.entitlement.Entitlement;
import github.muhsenerdev.plans.domain.entitlement.EntitlementRepository;
import github.muhsenerdev.plans.domain.entitlement.EntitlementSourceType;
import github.muhsenerdev.plans.domain.entitlement.EntitlementStatus;
import github.muhsenerdev.plans.domain.subscription.Subscription;
import github.muhsenerdev.plans.domain.subscription.Subscription.EntitlementsSnapshot;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EntitlementService {
    private final EntitlementRepository entitlementRepository;

    @Transactional
    public List<Entitlement> grantEntitlements(Subscription subscription) {
        // 1. Expire all active entitlements related to this subscription
        List<Entitlement> existingEntitlements = entitlementRepository
                .findAllBySubscriptionIdAndStatus(subscription.getId(), EntitlementStatus.ACTIVE);

        for (Entitlement entitlement : existingEntitlements) {
            entitlement.expire();
        }

        // 2. Create new entitlements
        List<Entitlement> newEntitlements = new ArrayList<>();
        OffsetDateTime validFrom = subscription.getEntitlementValidFrom();
        OffsetDateTime validTo = validFrom.plusMonths(1).plus(Subscription.GRACE_PERID_DURATION);

        Map<String, EntitlementsSnapshot> snapshots = subscription.getEntitlements();
        for (Map.Entry<String, EntitlementsSnapshot> entry : snapshots.entrySet()) {
            String featureCode = entry.getKey();
            EntitlementsSnapshot snapshot = entry.getValue();
            Integer totalAmount = parseAmount(snapshot.getValue());

            Entitlement newEntitlement = Entitlement.builder()
                    .userId(subscription.getUserId())
                    .subscriptionId(subscription.getId())
                    .featureCode(featureCode)
                    .featureType(snapshot.getType())
                    .totalAmount(totalAmount)
                    .usedAmount(0)
                    .sourceType(EntitlementSourceType.SUBSCRIPTION)
                    .status(EntitlementStatus.ACTIVE)
                    .validFrom(validFrom)
                    .validTo(validTo)
                    .build();

            newEntitlements.add(newEntitlement);
        }

        List<Entitlement> all = new ArrayList<>(existingEntitlements);
        all.addAll(newEntitlements);

        return entitlementRepository.saveAll(all);
    }

    @Transactional
    public void cancelEntitlements(Subscription subscription) {
        List<Entitlement> existingEntitlements = entitlementRepository
                .findAllBySubscriptionIdAndStatus(subscription.getId(), EntitlementStatus.ACTIVE);

        for (Entitlement entitlement : existingEntitlements) {
            entitlement.cancel();
        }

        entitlementRepository.saveAll(existingEntitlements);
    }

    private Integer parseAmount(String value) {
        if (value == null) {
            return null;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

}
