package github.muhsenerdev.plans.application.entitlement;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import github.muhsenerdev.commons.core.exception.NotFoundException;
import github.muhsenerdev.plans.domain.entitlement.Entitlement;
import github.muhsenerdev.plans.domain.entitlement.EntitlementRepository;
import github.muhsenerdev.plans.domain.entitlement.EntitlementSourceType;
import github.muhsenerdev.plans.domain.entitlement.EntitlementStatus;
import github.muhsenerdev.plans.domain.feature.FeatureRepository;
import github.muhsenerdev.plans.domain.feature.FeatureType;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EntitlementApplicationService {

    private final EntitlementRepository entitlementRepository;
    private final FeatureRepository featureRepository;

    @Transactional
    public void grantEntitlement(GrantEntitlementCommand command) {
        if (command.getSourceType() == EntitlementSourceType.SUBSCRIPTION) {
            throw new IllegalArgumentException("Subscription entitlement cannot be granted directly");
        }
        var coreFeature = featureRepository.findByCode(command.getFeatureCode())
                .orElseThrow(() -> new NotFoundException("Feature not found: " + command.getFeatureCode()));

        FeatureType featureType = coreFeature.getType();
        // IF duration is null, it means it's a permanent entitlement
        OffsetDateTime now = OffsetDateTime.now();
        OffsetDateTime validTo = command.getDuration() != null ? now.plus(command.getDuration()) : null;

        Integer totalAmount = null;
        Integer usedAmount = null;
        if (featureType == FeatureType.QUOTA) {
            if (command.getValue() == null) {
                throw new IllegalArgumentException("Value cannot be null for quota feature");
            }
            totalAmount = command.getValue();
            usedAmount = 0;
        }

        Entitlement entitlement = Entitlement.builder()
                .userId(command.getUserId())
                .featureCode(command.getFeatureCode())
                .featureType(featureType)
                .totalAmount(totalAmount)
                .usedAmount(usedAmount)
                .sourceType(command.getSourceType())
                .status(EntitlementStatus.ACTIVE)
                .validFrom(now)
                .validTo(validTo)
                .build();

        entitlementRepository.save(entitlement);
    }

    @Transactional(readOnly = true)
    public boolean hasAccess(UUID userId, String featureCode) {
        // Just check if there is ANY active entitlement for this feature
        List<Entitlement> entitlements = entitlementRepository.findAllActiveByUserIdAndFeatureCode(
                userId, featureCode, OffsetDateTime.now());

        return !entitlements.isEmpty();
    }

    @Transactional
    public QuotaReservationDto reserveQuota(UUID userId, String featureCode, int amount) {
        // 1. Lock rows for update
        List<Entitlement> entitlements = entitlementRepository.findBestEntitlementsForConsumption(
                userId, featureCode, OffsetDateTime.now());

        if (entitlements.isEmpty()) {
            throw new IllegalStateException("No active entitlement found for feature: " + featureCode);
        }

        // Validate feature type of the first entitlement (assuming consistent type for
        // same feature code)
        if (entitlements.get(0).getFeatureType() != FeatureType.QUOTA) {
            throw new IllegalArgumentException("Feature " + featureCode + " is not a quota feature");
        }

        List<QuotaReservationDto.UsageItem> usageItems = new java.util.ArrayList<>();
        int remainingToReserve = amount;

        for (Entitlement entitlement : entitlements) {
            if (entitlement.getTotalAmount() == null) {
                // Unlimited quota
                // We don't deduct from unlimited
                usageItems.add(new QuotaReservationDto.UsageItem(entitlement.getId(), amount));
                remainingToReserve = 0;
                break;
            }

            int currentUsed = entitlement.getUsedAmount() != null ? entitlement.getUsedAmount() : 0;
            int available = entitlement.getTotalAmount() - currentUsed;

            if (available > 0) {
                int portion = Math.min(available, remainingToReserve);
                entitlement.useAmount(portion);

                usageItems.add(new QuotaReservationDto.UsageItem(entitlement.getId(), portion));
                remainingToReserve -= portion;

                if (remainingToReserve <= 0) {
                    break;
                }
            }
        }

        if (remainingToReserve > 0) {
            throw new IllegalStateException(
                    "Insufficient quota for feature: " + featureCode + ". Missing: " + remainingToReserve);
        }

        entitlementRepository.saveAll(entitlements);

        return QuotaReservationDto.builder()
                .items(usageItems)
                .totalReserved(amount)
                .build();
    }
}
