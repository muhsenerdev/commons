package github.muhsenerdev.users.core.infra.adapter.rest;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import github.muhsenerdev.commons.web.config.BaseOpenApiConfig;
import github.muhsenerdev.users.core.application.user.register.RegisterUserCommand;
import github.muhsenerdev.users.core.application.user.register.UserRegistrationResponse;
import github.muhsenerdev.users.core.application.user.resend_code.CodeResendResponse;
import github.muhsenerdev.users.core.application.user.resend_code.ResendVerificationCodeCommand;
import github.muhsenerdev.users.core.application.user.shared.UserApplicationService;
import github.muhsenerdev.users.core.application.user.verify_email.VerifyEmailCommand;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequestMapping("/public/registration")
@RestController
@Tag(name = "Registration", description = "User registration endpoint")
@RequiredArgsConstructor
public class PublicRegistrationController {

    private final UserApplicationService service;

    @PostMapping
    @Operation(summary = "Register a new user", description = "Creates a new user account with the provided details. Returns the registration ID.", responses = {
            @ApiResponse(responseCode = "200", description = "User successfully registered"),
            @ApiResponse(responseCode = "400", ref = BaseOpenApiConfig.BAD_REQUEST_RESPONSE),
            @ApiResponse(responseCode = "500", ref = BaseOpenApiConfig.INTERNAL_SERVER_ERROR_RESPONSE)
    })
    public ResponseEntity<UserRegistrationResponse> register(@Valid @RequestBody RegisterUserCommand command) {
        return ResponseEntity.ok(service.registerUser(command));
    }

    @PostMapping("/{id}/verification/resend-code")
    @Operation(summary = "Resend verification code", description = "Generates and resends a new verification code to the user's email.", responses = {
            @ApiResponse(responseCode = "200", description = "Verification code successfully resent"),
            @ApiResponse(responseCode = "400", ref = BaseOpenApiConfig.BAD_REQUEST_RESPONSE),
            @ApiResponse(responseCode = "500", ref = BaseOpenApiConfig.INTERNAL_SERVER_ERROR_RESPONSE)
    })
    public ResponseEntity<CodeResendResponse> resendCode(@PathVariable(value = "id", required = true) UUID userId) {
        var response = service.resendCode(ResendVerificationCodeCommand.builder().userId(userId).build());
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/{id}/verification")
    @Operation(summary = "Verify user email", description = "Verifies the user's email address using the provided verification code.", responses = {
            @ApiResponse(responseCode = "200", description = "Email successfully verified"),
            @ApiResponse(responseCode = "400", ref = BaseOpenApiConfig.BAD_REQUEST_RESPONSE),
            @ApiResponse(responseCode = "404", ref = BaseOpenApiConfig.NOT_FOUND_RESPONSE),
            @ApiResponse(responseCode = "500", ref = BaseOpenApiConfig.INTERNAL_SERVER_ERROR_RESPONSE)
    })
    public ResponseEntity<Void> verifyEmail(@PathVariable(value = "id", required = true) UUID userId,
            @Valid @RequestBody VerifyEmailCommand command) {

        command.setUserId(userId);
        service.verifyEmail(command);

        return ResponseEntity.ok().build();
    }
}
