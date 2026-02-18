package github.muhsenerdev.plans.application.plan.price.delete_price;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import github.muhsenerdev.plans.application.plan.shared.PlanService;
import github.muhsenerdev.plans.domain.plan.Plan;
import github.muhsenerdev.plans.domain.plan.PlanRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DeletePriceCommandHandler {

    private final PlanService planService;
    private final PlanRepository planRepository;

    @Transactional
    public void handle(DeletePriceCommand command) {
        Plan plan = planService.findWithPricesOrThrow(command.getPlanId());
        plan.prices().delete(command.getPriceId());
        planRepository.save(plan);
    }
}
