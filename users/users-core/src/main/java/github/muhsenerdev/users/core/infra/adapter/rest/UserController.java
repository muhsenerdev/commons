package github.muhsenerdev.users.core.infra.adapter.rest;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import github.muhsenerdev.commons.core.auth.Principal;
import github.muhsenerdev.users.core.application.user.change_password.ChangePasswordCommand;
import github.muhsenerdev.users.core.application.user.shared.UserApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "User Management", description = "Endpoints for user management")
public class UserController {

    private final UserApplicationService userApplicationService;

    @PutMapping("/me/password")
    @Operation(summary = "Change own password", description = "Allows the current user to change their own password")
    public void changePassword(@AuthenticationPrincipal Principal principal,
            @Valid @RequestBody ChangePasswordCommand command) {
        command.setUserId(principal.getUserId());
        command.setAdminChanges(false);
        userApplicationService.changePassword(command);
    }
}
