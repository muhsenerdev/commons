package github.muhsenerdev.plans.application.plan.shared;

import java.util.UUID;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import github.muhsenerdev.commons.core.exception.NotFoundException;
import github.muhsenerdev.plans.domain.plan.Plan;
import github.muhsenerdev.plans.domain.plan.PlanRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PlanService {

    private final PlanRepository planRepository;

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
        plan.reserveForActivation();
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

}
