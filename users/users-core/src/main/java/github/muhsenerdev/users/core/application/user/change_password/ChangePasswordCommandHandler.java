package github.muhsenerdev.users.core.application.user.change_password;

import java.util.Objects;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import github.muhsenerdev.commons.core.exception.InvalidDomainException;
import github.muhsenerdev.users.core.domain.users.UserDomainService;
import github.muhsenerdev.users.core.domain.users.UserRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ChangePasswordCommandHandler {

    private final UserRepository userRepository;
    private final UserDomainService userDomainService;

    @Transactional
    public void handle(ChangePasswordCommand command) {
        var userId = Objects.requireNonNull(command.getUserId(), "userId.required");
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new InvalidDomainException("User not found: " + userId));

        userDomainService.changePassword(user, command.getNewPassword(), command.getOldPassword(),
                command.isAdminChanges());

        userRepository.save(Objects.requireNonNull(user));
    }
}
