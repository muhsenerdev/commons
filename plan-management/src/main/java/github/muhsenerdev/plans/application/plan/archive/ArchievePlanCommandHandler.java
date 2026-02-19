package github.muhsenerdev.plans.application.plan.archive;

import org.springframework.stereotype.Component;

import github.muhsenerdev.plans.application.plan.shared.PlanService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ArchievePlanCommandHandler {

    private final PlanService planService;

    @Transactional
    public void handle(ArchievePlanCommand command) {
        planService.findWithPrices(command.planId())
                .ifPresent(p -> {
                    p.reserveForArchiving();
                    planService.save(p);
                });
    }
}
