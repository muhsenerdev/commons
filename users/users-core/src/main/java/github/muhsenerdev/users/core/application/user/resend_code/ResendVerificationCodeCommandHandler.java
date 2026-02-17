package github.muhsenerdev.users.core.application.user.resend_code;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import github.muhsenerdev.commons.core.exception.NotFoundException;
import github.muhsenerdev.users.core.application.user.shared.UserMapper;
import github.muhsenerdev.users.core.domain.users.User;
import github.muhsenerdev.users.core.domain.users.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ResendVerificationCodeCommandHandler {

    private final ApplicationEventPublisher eventPublisher;
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional
    @SuppressWarnings("null")
    public CodeResendResponse handle(@Valid ResendVerificationCodeCommand command) {
        User user = userRepository.findById(command.getUserId())
                .orElseThrow(() -> new NotFoundException("user.not-found",
                        "User not found with id: " + command.getUserId()));

        user.resendVerificationCode();
        userRepository.save(user);
        user.releaseEvents().forEach(eventPublisher::publishEvent);

        return userMapper.toCodeResendResponse(user.getEmailVerification());
    }
}
