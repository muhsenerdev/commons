package github.muhsenerdev.users.core.infra.adapter.rest;

import java.util.UUID;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import github.muhsenerdev.users.core.application.user.change_password.ChangePasswordCommand;
import github.muhsenerdev.users.core.application.user.shared.UserApplicationService;
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

    @PutMapping("/{id}/password")
    @Operation(summary = "Change any user's password", description = "Allows an administrator to change any user's password without needing the old one")
    public void changePassword(@PathVariable UUID id, @Valid @RequestBody ChangePasswordCommand command) {
        command.setUserId(id);
        command.setAdminChanges(true);
        userApplicationService.changePassword(command);
    }
}
