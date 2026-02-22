package github.muhsenerdev.plans.infra.messaging.in;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import github.muhsenerdev.plans.application.user.UserRegistrationCompletedHandler;
import github.muhsenerdev.plans.infra.config.PlanModuleProperties;
import github.muhsenerdev.users.api.application.registration.RegistrationCompletedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserRegistrationEventListener {

    private final PlanModuleProperties properties;
    private final UserRegistrationCompletedHandler handler;

    @EventListener
    public void onRegistrationCompleted(RegistrationCompletedEvent event) {
        if (!properties.getDefaultPlan().isEnabled()) {
            return;
        }

        log.info("Received RegistrationCompletedEvent for user: {}. Starting default plan subscription.",
                event.getUserId());
        try {
            handler.handle(event.getUserId());
        } catch (Exception e) {
            log.error("Failed to subscribe user {} to default plan", event.getUserId(), e);
        }
    }
}
