package github.muhsenerdev.users.core.infra.adapter.rest;

import java.util.UUID;

import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import github.muhsenerdev.users.core.application.user.change_password.ChangePasswordCommand;
import github.muhsenerdev.users.core.application.user.change_roles.PutUserRolesCommand;
import github.muhsenerdev.users.core.application.user.list.ListUsersQuery;
import github.muhsenerdev.users.core.application.user.list.UserListItem;
import github.muhsenerdev.users.core.application.user.register.RegisterUserCommand;
import github.muhsenerdev.users.core.application.user.register.UserRegistrationResponse;
import github.muhsenerdev.users.core.application.user.shared.UserApplicationService;
import github.muhsenerdev.users.core.domain.users.RegistrationType;
import github.muhsenerdev.users.core.domain.users.UserFilter;
import github.muhsenerdev.users.core.domain.users.UserStatus;
import github.muhsenerdev.users.core.domain.users.VerificationStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
@Tag(name = "User Admin", description = "Endpoints for user administration")
public class UserAdminController {

    private final UserApplicationService userApplicationService;

    @GetMapping
    @Operation(summary = "List all users", description = "Returns a paginated list of users with filtering support")
    public Page<UserListItem> listUsers(
            @RequestParam(required = false) UserStatus status,
            @RequestParam(required = false) VerificationStatus verificationStatus,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) RegistrationType registrationType,
            @ParameterObject Pageable pageable) {

        UserFilter filter = UserFilter.builder()
                .status(status)
                .verificationStatus(verificationStatus)
                .email(email)
                .registrationType(registrationType)
                .build();

        return userApplicationService.listUsers(ListUsersQuery.builder()
                .filter(filter)
                .pageable(pageable)
                .build());
    }

    @PutMapping("/{id}/password")
    @Operation(summary = "Change any user's password", description = "Allows an administrator to change any user's password without needing the old one")
    public void changePassword(@PathVariable UUID id, @Valid @RequestBody ChangePasswordCommand command) {
        command.setUserId(id);
        command.setAdminChanges(true);
        userApplicationService.changePassword(command);
    }

    @PostMapping
    @Operation(summary = "Create a new user", description = "Allows an administrator to create a new user")
    public UserRegistrationResponse createUser(@Valid @RequestBody RegisterUserCommand command) {
        command = command.toBuilder().verified(true).build();
        return userApplicationService.registerUser(command);
    }

    @PutMapping("/{id}/roles")
    @Operation(summary = "Update user roles", description = "Allows an administrator to update the roles of a user")
    public void changeRoles(@PathVariable UUID id, @Valid @RequestBody PutUserRolesCommand command) {
        command.setUserId(id);
        userApplicationService.changeRoles(command);
    }
}
