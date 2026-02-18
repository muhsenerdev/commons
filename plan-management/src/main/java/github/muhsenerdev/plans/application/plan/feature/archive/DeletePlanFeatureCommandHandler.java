package github.muhsenerdev.plans.application.plan.feature.archive;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import github.muhsenerdev.plans.application.plan.shared.PlanService;
import github.muhsenerdev.plans.domain.plan.Plan;
import github.muhsenerdev.plans.domain.plan.PlanRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DeletePlanFeatureCommandHandler {

    private final PlanService planService;
    private final PlanRepository planRepository;

    @Transactional
    public void handle(DeletePlanFeatureCommand command) {
        Plan plan = planService.findWithFeaturesOrThrow(command.getPlanId());
        plan.features().delete(command.getPlanFeatureId());
        planRepository.save(plan);
    }
}
