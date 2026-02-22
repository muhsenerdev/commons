package github.muhsenerdev.plans.infra.worker;

import github.muhsenerdev.plans.application.shared.CheckoutExpirationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class CheckoutExpirationWorker {

    private final CheckoutExpirationService checkoutExpirationService;

    @Scheduled(fixedDelayString = "${app.subscription.checkout-expiration-interval:PT15M}")
    public void run() {
        log.debug("Starting checkout expiration worker run...");
        try {
            checkoutExpirationService.expirePendingCheckouts();
        } catch (Exception e) {
            log.error("Error during checkout expiration worker execution", e);
        }
    }
}
