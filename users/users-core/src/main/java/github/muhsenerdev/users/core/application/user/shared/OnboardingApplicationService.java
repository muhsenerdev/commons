package github.muhsenerdev.users.core.application.user.shared;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import github.muhsenerdev.commons.core.exception.NotFoundException;
import github.muhsenerdev.users.core.application.user.resend_code.CodeResendResponse;
import github.muhsenerdev.users.core.application.user.resend_code.ResendVerificationCodeCommand;
import github.muhsenerdev.users.core.domain.users.User;
import github.muhsenerdev.users.core.domain.users.UserRepository;
import github.muhsenerdev.users.core.application.user.verify_email.VerifyEmailCommand;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OnboardingApplicationService {

    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final UserMapper userMapper;

    @Transactional
    @SuppressWarnings("null")
    public CodeResendResponse resendCode(@Valid ResendVerificationCodeCommand command) {
        User user = userRepository.findById(command.getUserId())
                .orElseThrow(() -> new NotFoundException("user.not-found",
                        "User not found with id: " + command.getUserId()));

        user.resendVerificationCode();
        userRepository.save(user);

        eventPublisher.publishEvent(userMapper.toVerificationCodeResentEvent(user));

        return userMapper.toCodeResendResponse(user.getEmailVerification());
    }

    @Transactional
    @SuppressWarnings("null")
    public void verifyEmail(@Valid VerifyEmailCommand command) {
        User user = userRepository.findById(command.getUserId())
                .orElseThrow(() -> new NotFoundException("user.not-found",
                        "User not found with id: " + command.getUserId()));

        user.verifyEmail(command.getCode());
        userRepository.save(user);

        eventPublisher.publishEvent(userMapper.toEmailVerifiedEvent(user));
    }

}
