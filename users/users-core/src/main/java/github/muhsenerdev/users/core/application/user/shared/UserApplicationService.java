package github.muhsenerdev.users.core.application.user.shared;

import github.muhsenerdev.users.core.application.user.change_password.ChangePasswordCommand;
import github.muhsenerdev.users.core.application.user.complete.CompleteRegistrationCommand;
import github.muhsenerdev.users.core.application.user.password_reset.CompletePasswordResetCommand;
import github.muhsenerdev.users.core.application.user.password_reset.RequestPasswordResetCommand;
import github.muhsenerdev.users.core.application.user.change_roles.PutUserRolesCommand;
import github.muhsenerdev.users.core.application.user.list.ListUsersQuery;
import github.muhsenerdev.users.core.application.user.list.UserListItem;
import github.muhsenerdev.users.core.application.user.register.RegisterUserCommand;
import github.muhsenerdev.users.core.application.user.register.UserRegistrationResponse;
import github.muhsenerdev.users.core.application.user.resend_code.CodeResendResponse;
import github.muhsenerdev.users.core.application.user.resend_code.ResendVerificationCodeCommand;
import github.muhsenerdev.users.core.application.user.verify_email.VerifyEmailCommand;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;

public interface UserApplicationService {

    UserRegistrationResponse registerUser(@Valid RegisterUserCommand command);

    CodeResendResponse resendCode(@Valid ResendVerificationCodeCommand command);

    void verifyEmail(@Valid VerifyEmailCommand command);

    void completeRegistration(@Valid CompleteRegistrationCommand command);

    void changePassword(@Valid ChangePasswordCommand command);

    void requestPasswordReset(@Valid RequestPasswordResetCommand command);

    void completePasswordReset(@Valid CompletePasswordResetCommand command);

    Page<UserListItem> listUsers(ListUsersQuery query);

    void changeRoles(@Valid PutUserRolesCommand command);
}
