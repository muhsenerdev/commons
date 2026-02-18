package github.muhsenerdev.plans.application.plan.update;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import github.muhsenerdev.commons.core.exception.NotFoundException;
import github.muhsenerdev.plans.application.plan.shared.PlanMapper;
import github.muhsenerdev.plans.domain.plan.Plan;
import github.muhsenerdev.plans.domain.plan.PlanDomainService;
import github.muhsenerdev.plans.domain.plan.PlanRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UpdatePlanCommandHandler {

    private final PlanDomainService planDomainService;
    private final PlanRepository planRepository;
    private final PlanMapper planMapper;

    @Transactional
    public void handle(UpdatePlanCommand command) {
        Plan plan = planRepository.findById(command.getId())
                .orElseThrow(
                        () -> new NotFoundException("plan.not_found", "Plan not found with id: " + command.getId()));

        planDomainService.updatePlan(plan, planMapper.toInput(command));
        planRepository.save(plan);
    }
}
