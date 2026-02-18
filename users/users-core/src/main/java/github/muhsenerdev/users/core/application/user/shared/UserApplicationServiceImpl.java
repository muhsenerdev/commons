package github.muhsenerdev.users.core.application.user.shared;

import org.springframework.stereotype.Service;

import github.muhsenerdev.users.core.application.user.change_password.ChangePasswordCommand;
import github.muhsenerdev.users.core.application.user.change_password.ChangePasswordCommandHandler;
import github.muhsenerdev.users.core.application.user.change_roles.PutUserRolesCommand;
import github.muhsenerdev.users.core.application.user.change_roles.PutUserRolesCommandHandler;
import github.muhsenerdev.users.core.application.user.complete.CompleteRegistrationCommand;
import github.muhsenerdev.users.core.application.user.complete.CompleteRegistrationCommandHandler;
import github.muhsenerdev.users.core.application.user.list.ListUsersQuery;
import github.muhsenerdev.users.core.application.user.list.ListUsersQueryHandler;
import github.muhsenerdev.users.core.application.user.list.UserListItem;
import github.muhsenerdev.users.core.application.user.password_reset.CompletePasswordResetCommand;
import github.muhsenerdev.users.core.application.user.password_reset.CompletePasswordResetCommandHandler;
import github.muhsenerdev.users.core.application.user.password_reset.RequestPasswordResetCommand;
import github.muhsenerdev.users.core.application.user.password_reset.RequestPasswordResetCommandHandler;
import github.muhsenerdev.users.core.application.user.register.RegisterUserCommand;
import github.muhsenerdev.users.core.application.user.register.RegisterUserCommandHandler;
import github.muhsenerdev.users.core.application.user.register.UserRegistrationResponse;
import github.muhsenerdev.users.core.application.user.resend_code.CodeResendResponse;
import github.muhsenerdev.users.core.application.user.resend_code.ResendVerificationCodeCommand;
import github.muhsenerdev.users.core.application.user.resend_code.ResendVerificationCodeCommandHandler;
import github.muhsenerdev.users.core.application.user.verify_email.VerifyEmailCommand;
import github.muhsenerdev.users.core.application.user.verify_email.VerifyEmailCommandHandler;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;

@Service
@RequiredArgsConstructor
public class UserApplicationServiceImpl implements UserApplicationService {

    private final RegisterUserCommandHandler registerUser;
    private final ResendVerificationCodeCommandHandler resendCode;
    private final VerifyEmailCommandHandler verifyEmail;
    private final CompleteRegistrationCommandHandler completeRegistration;
    private final ChangePasswordCommandHandler changePassword;
    private final RequestPasswordResetCommandHandler requestPasswordReset;
    private final CompletePasswordResetCommandHandler completePasswordReset;
    private final ListUsersQueryHandler listUsers;
    private final PutUserRolesCommandHandler putUserRoles;

    @Override
    public UserRegistrationResponse registerUser(RegisterUserCommand command) {
        return registerUser.handle(command);
    }

    @Override
    public CodeResendResponse resendCode(ResendVerificationCodeCommand command) {
        return resendCode.handle(command);
    }

    @Override
    public void verifyEmail(VerifyEmailCommand command) {
        verifyEmail.handle(command);
    }

    @Override
    public void completeRegistration(@Valid CompleteRegistrationCommand command) {
        completeRegistration.handle(command);
    }

    @Override
    public void changePassword(@Valid ChangePasswordCommand command) {
        changePassword.handle(command);
    }

    @Override
    public void requestPasswordReset(@Valid RequestPasswordResetCommand command) {
        requestPasswordReset.handle(command);
    }

    @Override
    public void completePasswordReset(@Valid CompletePasswordResetCommand command) {
        completePasswordReset.handle(command);
    }

    @Override
    public Page<UserListItem> listUsers(ListUsersQuery query) {
        return listUsers.handle(query);
    }

    @Override
    public void changeRoles(PutUserRolesCommand command) {
        putUserRoles.handle(command);
    }

}
