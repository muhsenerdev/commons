package github.muhsenerdev.users.core.application.user.register;

import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import github.muhsenerdev.users.core.domain.users.User;
import github.muhsenerdev.users.core.domain.users.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class RegistrationExpirationService {

    private final UserRepository userRepository;

    @Transactional
    public void cleanupExpiredRegistrations() {
        log.debug("Checking for expired registrations...");
        List<User> expiredUsers = userRepository.findExpiredRegistrations(OffsetDateTime.now());

        if (expiredUsers.isEmpty()) {
            return;
        }

        log.info("Found {} expired registrations. Soft-deleting...", expiredUsers.size());
        userRepository.deleteAll(expiredUsers);
        log.info("Successfully cleaned up expired registrations.");
    }
}
