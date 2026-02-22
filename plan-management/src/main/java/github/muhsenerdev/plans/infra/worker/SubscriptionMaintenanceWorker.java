package github.muhsenerdev.plans.infra.worker;

import github.muhsenerdev.plans.application.shared.SubscriptionMaintenanceProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SubscriptionMaintenanceWorker {

    private final SubscriptionMaintenanceProcessor processor;

    @Scheduled(fixedRateString = "${app.subscription.maintenance-interval:PT2H}")
    public void run() {
        log.info("Starting subscription maintenance worker run...");
        try {
            processor.process();
        } catch (Exception e) {
            log.error("Failed to run subscription maintenance processor", e);
        }
        log.info("Finished subscription maintenance worker run.");
    }
}
