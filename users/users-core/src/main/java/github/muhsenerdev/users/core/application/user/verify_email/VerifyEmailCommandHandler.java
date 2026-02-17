package github.muhsenerdev.users.core.application.user.verify_email;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import github.muhsenerdev.commons.core.exception.NotFoundException;
import github.muhsenerdev.users.core.domain.users.User;
import github.muhsenerdev.users.core.domain.users.UserRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor

public class VerifyEmailCommandHandler {

    private final ApplicationEventPublisher eventPublisher;
    private final UserRepository userRepository;

    @Transactional
    public void handle(VerifyEmailCommand command) {
        User user = userRepository.findById(command.getUserId())
                .orElseThrow(() -> new NotFoundException("user.not-found",
                        "User not found with id: " + command.getUserId()));

        user.verifyEmail(command.getCode());
        userRepository.save(user);
        user.releaseEvents().forEach(eventPublisher::publishEvent);
    }

}
