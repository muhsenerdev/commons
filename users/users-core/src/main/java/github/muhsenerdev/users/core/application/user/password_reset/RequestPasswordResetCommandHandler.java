package github.muhsenerdev.users.core.application.user.password_reset;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import github.muhsenerdev.commons.core.exception.NotFoundException;
import github.muhsenerdev.commons.core.vo.Email;
import github.muhsenerdev.users.core.domain.users.UserRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RequestPasswordResetCommandHandler {

    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public void handle(RequestPasswordResetCommand command) {
        var user = userRepository.findByEmail(Email.of(command.getEmail()))
                .orElseThrow(() -> new NotFoundException("user.not_found",
                        "User not found with email: " + command.getEmail()));

        user.requestPasswordReset();

        userRepository.save(user);
        user.releaseEvents().forEach(eventPublisher::publishEvent);
    }
}
