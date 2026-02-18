package github.muhsenerdev.plans.application.plan.feature.add;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import github.muhsenerdev.commons.core.exception.NotFoundException;
import github.muhsenerdev.plans.application.plan.shared.PlanService;
import github.muhsenerdev.plans.domain.feature.Feature;
import github.muhsenerdev.plans.domain.feature.FeatureRepository;
import github.muhsenerdev.plans.domain.plan.Plan;
import github.muhsenerdev.plans.domain.plan.PlanRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AddPlanFeatureCommandHandler {

    private final PlanService planService;
    private final FeatureRepository featureRepository;
    private final PlanRepository planRepository;

    @Transactional
    public void handle(AddPlanFeatureCommand command) {
        Plan plan = planService.findWithFeaturesOrThrow(command.getPlanId());
        Feature feature = featureRepository.findById(command.getFeatureId())
                .orElseThrow(() -> new NotFoundException("feature.not_found",
                        "Feature not found with id: " + command.getFeatureId()));

        plan.features().add(feature, command.getValue());
        planRepository.save(plan);
    }
}
