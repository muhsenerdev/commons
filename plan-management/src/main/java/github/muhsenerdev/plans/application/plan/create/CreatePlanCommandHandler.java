package github.muhsenerdev.plans.application.plan.create;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import github.muhsenerdev.plans.application.plan.shared.PlanMapper;
import github.muhsenerdev.plans.domain.plan.Plan;
import github.muhsenerdev.plans.domain.plan.PlanDomainService;
import github.muhsenerdev.plans.domain.plan.PlanRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CreatePlanCommandHandler {

    private final PlanDomainService planDomainService;
    private final PlanRepository planRepository;
    private final PlanMapper planMapper;

    @Transactional
    public PlanCreationResponse handle(CreatePlanCommand command) {
        Plan plan = planDomainService.createPlan(planMapper.toCreationInput(command));
        planRepository.save(plan);
        return PlanCreationResponse.builder().id(plan.getId()).build();
    }
}
