package github.muhsenerdev.plans.application.plan.feature.activate;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import github.muhsenerdev.plans.application.plan.shared.PlanService;
import github.muhsenerdev.plans.domain.plan.Plan;
import github.muhsenerdev.plans.domain.plan.PlanRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ActivatePlanFeatureCommandHandler {

    private final PlanService planService;
    private final PlanRepository planRepository;

    @Transactional
    public void handle(ActivatePlanFeatureCommand command) {
        Plan plan = planService.findWithFeaturesOrThrow(command.getPlanId());
        plan.features().activate(command.getPlanFeatureId());
        planRepository.save(plan);

    }
}
