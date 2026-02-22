package github.muhsenerdev.plans.application.plan.shared;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import github.muhsenerdev.commons.core.exception.NotFoundException;
import github.muhsenerdev.plans.domain.plan.Plan;
import github.muhsenerdev.plans.domain.plan.PlanDomainService;
import github.muhsenerdev.plans.domain.plan.PlanRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PlanService {

    private final PlanRepository planRepository;
    private final PlanDomainService planDomainService;

    @Transactional
    public Plan findByIdOrThrow(UUID id) {
        Assert.notNull(id, "PlanId cannot be null!");
        return planRepository.findById(id)
                .orElseThrow(
                        () -> new NotFoundException("plan.not_found", "Plan not found with id: " + id));
    }

    @Transactional
    public Plan findWithPricesOrThrow(UUID id) {
        Assert.notNull(id, "PlanId cannot be null!");
        return planRepository.findWithPricesById(id)
                .orElseThrow(
                        () -> new NotFoundException("plan.not_found", "Plan not found with id: " + id));
    }

    public Optional<Plan> findWithPrices(UUID id) {
        Assert.notNull(id, "PlanId cannot be null!");
        return planRepository.findWithPricesById(id);
    }

    @Transactional
    public Plan findWithFeaturesOrThrow(UUID id) {
        Assert.notNull(id, "PlanId cannot be null!");
        return planRepository.findWithFeaturesDeeplyById(id)
                .orElseThrow(
                        () -> new NotFoundException("plan.not_found", "Plan not found with id: " + id));
    }

    @Transactional
    public Plan reserveForActivation(UUID id) {
        Plan plan = findWithFeaturesOrThrow(id);
        planDomainService.reserveForActivation(plan);
        return planRepository.save(plan);
    }

    @Transactional
    public void finalizeActivation(UUID id, String providerId, Map<UUID, String> priceProviderIds) {
        Plan plan = findWithPricesOrThrow(id);
        plan.activate(providerId, priceProviderIds);
        planRepository.save(plan);
    }

    @Transactional
    public void markAsFailed(UUID id, String reason) {
        planRepository.findById(id).ifPresent(plan -> {
            plan.activationFailed(reason);
            planRepository.save(plan);
        });
    }

    @Transactional
    public Plan reservePriceForActivation(UUID planId, UUID priceId, boolean overrideActivePrice) {
        Plan plan = findWithPricesOrThrow(planId);
        plan.prices().reserveForActivation(priceId, overrideActivePrice);
        return planRepository.save(plan);
    }

    @Transactional
    public void finalizePriceActivation(UUID planId, UUID priceId, String providerId) {
        Plan plan = findWithPricesOrThrow(planId);
        plan.prices().finalizeActivation(priceId, providerId);
        planRepository.save(plan);
    }

    @Transactional
    public void markPriceAsFailed(UUID planId, UUID priceId, String reason) {
        planRepository.findWithPricesById(planId).ifPresent(plan -> {
            plan.prices().markAsFailed(priceId, reason);
            planRepository.save(plan);
        });
    }

    @Transactional
    public void reservePriceForArchive(UUID planId, UUID priceId) {
        planRepository.findWithPricesById(planId).ifPresent(plan -> {
            plan.prices().reserveForArchive(priceId);
            planRepository.save(plan);
        });
    }

    @Transactional
    public Plan save(Plan plan) {
        return planRepository.save(plan);
    }

}
