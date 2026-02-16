package github.muhsenerdev.users.core.application.user.register;

import org.springframework.stereotype.Service;

import github.muhsenerdev.users.api.application.registration.RegisterUserBaseCommand;
import github.muhsenerdev.users.api.application.registration.UserRegistrationStrategy;
import lombok.extern.slf4j.Slf4j;

@Service

@Slf4j
public class NoOpRegistrationStrategy implements UserRegistrationStrategy<RegisterUserBaseCommand> {

    @Override
    public void validate(RegisterUserBaseCommand command) {
        log.debug("No-op registration strategy is called.");
        log.debug("Command: {}", command);
    }

}
