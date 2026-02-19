package github.muhsenerdev.plans.application.plan.delete;

import github.muhsenerdev.plans.application.plan.shared.PlanService;
import github.muhsenerdev.plans.domain.plan.Plan;
import github.muhsenerdev.plans.domain.plan.PlanRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class DeletePlanCommandHandler {

    private final PlanService planService;
    private final PlanRepository planRepository;

    @Transactional
    public void handle(DeletePlanCommand command) {
        Plan plan = planService.findByIdOrThrow(command.getPlanId());
        plan.delete();
        planRepository.delete(plan);
    }
}
