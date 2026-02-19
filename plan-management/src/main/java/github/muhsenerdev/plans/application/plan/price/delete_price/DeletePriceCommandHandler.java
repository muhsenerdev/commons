package github.muhsenerdev.plans.application.plan.price.delete_price;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import github.muhsenerdev.plans.application.plan.shared.PlanService;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class DeletePriceCommandHandler {

    private final PlanService planService;

    @Transactional
    public void handle(DeletePriceCommand command) {
        planService.findWithPrices(command.getPlanId())
                .ifPresent(plan -> {
                    plan.prices().delete(command.getPriceId());
                    planService.save(plan);
                });
    }
}
