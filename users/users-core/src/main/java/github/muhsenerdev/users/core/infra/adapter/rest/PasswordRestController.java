package github.muhsenerdev.users.core.infra.adapter.rest;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import github.muhsenerdev.users.core.application.user.password_reset.CompletePasswordResetCommand;
import github.muhsenerdev.users.core.application.user.password_reset.RequestPasswordResetCommand;
import github.muhsenerdev.users.core.application.user.shared.UserApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/public/password-reset")
@RequiredArgsConstructor
@Tag(name = "Password Reset", description = "Endpoints for password reset flow")
public class PasswordRestController {

    private final UserApplicationService userApplicationService;

    @Operation(summary = "Request password reset", description = "Initiates password reset flow by sending a code to user's email.")
    @PostMapping
    public void requestPasswordReset(@Valid @RequestBody RequestPasswordResetCommand command) {
        userApplicationService.requestPasswordReset(command);
    }

    @Operation(summary = "Complete password reset", description = "Resets user password using the code sent to email.")
    @PostMapping("/complete")
    public void completePasswordReset(@Valid @RequestBody CompletePasswordResetCommand command) {
        userApplicationService.completePasswordReset(command);
    }
}
