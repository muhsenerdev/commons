package github.muhsenerdev.users.core.application.user.complete;

import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import github.muhsenerdev.commons.core.exception.NotFoundException;
import github.muhsenerdev.commons.core.vo.Name;
import github.muhsenerdev.commons.core.vo.Username;
import github.muhsenerdev.users.api.application.registration.RegistrationHook;
import github.muhsenerdev.users.core.domain.users.User;
import github.muhsenerdev.users.core.domain.users.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class CompleteRegistrationCommandHandler {

    private final UserRepository userRepository;

    private final List<RegistrationHook> registrationHooks;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public void handle(CompleteRegistrationCommand command) {
        log.info("Completing registration for user: {}", command.getUserId());

        User user = userRepository.findById(command.getUserId())
                .orElseThrow(() -> new NotFoundException("user.not_found", "User not found: " + command.getUserId()));

        // 1. Hook for sub-modules to validate custom details
        registrationHooks.forEach(strategy -> strategy.validateCompletionDetails(command.getOtherDetails()));

        // 2. Local validation happens naturally via VO creation
        Name name = Name.fromStringOrNull(command.getName());
        Username username = Username.fromStringOrNull(command.getUsername());

        // 3. Update domain entity
        user.completeRegistration(name, username, command.getOtherDetails());

        // 4. Persistence
        userRepository.save(user);

        // 5. Publish events (e.g., UserActivatedEvent)
        user.releaseEvents().forEach(eventPublisher::publishEvent);

        log.info("Registration completed successfully for user: {}", command.getUserId());
    }

}
