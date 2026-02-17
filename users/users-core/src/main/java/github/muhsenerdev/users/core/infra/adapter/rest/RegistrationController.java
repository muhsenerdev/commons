package github.muhsenerdev.users.core.infra.adapter.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import github.muhsenerdev.commons.core.auth.Principal;
import github.muhsenerdev.users.core.application.user.complete.CompleteRegistrationCommand;
import github.muhsenerdev.users.core.application.user.shared.UserApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequestMapping("/registration")
@RestController
@Tag(name = "Registration", description = "User registration endpoint")
@RequiredArgsConstructor
public class RegistrationController {

    private final UserApplicationService service;

    @PostMapping("/complete")
    @Operation(summary = "Complete user registration", description = "Provides missing details to activate user account")
    public ResponseEntity<Void> complete(
            @AuthenticationPrincipal Principal principal,
            @Valid @RequestBody CompleteRegistrationCommand command) {

        command = command.toBuilder().userId(principal.getUserId()).build();

        service.completeRegistration(command);
        return ResponseEntity.noContent().build();
    }

}
