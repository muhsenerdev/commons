package github.muhsenerdev.plans.application.plan.price.archive;

import github.muhsenerdev.plans.application.plan.shared.PlanService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ArchivePriceCommandHandler {

    private final PlanService planService;

    public void handle(ArchivePriceCommand command) {
        log.info("Archiving price {} for plan {}", command.getPriceId(), command.getPlanId());
        planService.reservePriceForArchive(command.getPlanId(), command.getPriceId());
    }
}
