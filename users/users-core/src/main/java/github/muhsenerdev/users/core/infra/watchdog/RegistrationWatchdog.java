package github.muhsenerdev.users.core.infra.watchdog;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import github.muhsenerdev.users.core.application.user.register.RegistrationExpirationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class RegistrationWatchdog {

    private final RegistrationExpirationService registrationExpirationService;

    @Scheduled(fixedDelayString = "${app.users.watchdog.registration-cleanup-interval:PT5M}")
    public void cleanupExpiredRegistrations() {
        log.debug("Watchdog: Triggering expired registrations cleanup.");
        registrationExpirationService.cleanupExpiredRegistrations();
    }
}
