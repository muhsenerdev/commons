package github.muhsenerdev.users.core.application.user.shared;

import github.muhsenerdev.users.core.application.user.change_password.ChangePasswordCommand;
import github.muhsenerdev.users.core.application.user.complete.CompleteRegistrationCommand;
import github.muhsenerdev.users.core.application.user.register.RegisterUserCommand;
import github.muhsenerdev.users.core.application.user.register.UserRegistrationResponse;
import github.muhsenerdev.users.core.application.user.resend_code.CodeResendResponse;
import github.muhsenerdev.users.core.application.user.resend_code.ResendVerificationCodeCommand;
import github.muhsenerdev.users.core.application.user.verify_email.VerifyEmailCommand;
import jakarta.validation.Valid;

public interface UserApplicationService {

    public UserRegistrationResponse registerUser(RegisterUserCommand command);

    public CodeResendResponse resendCode(ResendVerificationCodeCommand command);

    public void verifyEmail(VerifyEmailCommand command);

    public void completeRegistration(@Valid CompleteRegistrationCommand command);

    public void changePassword(@Valid ChangePasswordCommand command);

}
