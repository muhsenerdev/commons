package github.muhsenerdev.users.core.application.user.password_reset;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import github.muhsenerdev.commons.core.exception.InvalidDomainException;
import github.muhsenerdev.commons.core.vo.Email;
import github.muhsenerdev.users.core.domain.shared.PasswordService;
import github.muhsenerdev.users.core.domain.users.HashedPassword;
import github.muhsenerdev.users.core.domain.users.UserRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CompletePasswordResetCommandHandler {

    private final UserRepository userRepository;
    private final PasswordService passwordService;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public void handle(CompletePasswordResetCommand command) {
        var user = userRepository.findByEmail(Email.of(command.getEmail()))
                .orElseThrow(() -> new InvalidDomainException("User not found: " + command.getEmail()));

        passwordService.validate(command.getNewPassword());
        HashedPassword hashedPassword = passwordService.hash(command.getNewPassword());

        user.completePasswordReset(command.getCode(), hashedPassword);

        userRepository.save(user);
        user.releaseEvents().forEach(eventPublisher::publishEvent);
    }
}
