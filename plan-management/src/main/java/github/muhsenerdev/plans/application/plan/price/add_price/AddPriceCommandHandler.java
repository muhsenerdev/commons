package github.muhsenerdev.plans.application.plan.price.add_price;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import github.muhsenerdev.plans.application.plan.shared.PlanMapper;
import github.muhsenerdev.plans.application.plan.shared.PlanService;
import github.muhsenerdev.plans.domain.plan.Plan;
import github.muhsenerdev.plans.domain.plan.PlanRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AddPriceCommandHandler {

    private final PlanRepository planRepository;
    private final PlanMapper planMapper;
    private final PlanService planService;

    @Transactional
    public AddPriceResponse handle(AddPriceCommand command) {
        Plan plan = planService.findWithPricesOrThrow(command.getId());

        plan.prices().add(planMapper.extractMoney(command), command.getInterval());
        planRepository.save(plan);

        return AddPriceResponse.builder()
                .planPriceId(plan.prices().getLast().getId())
                .build();
    }
}
