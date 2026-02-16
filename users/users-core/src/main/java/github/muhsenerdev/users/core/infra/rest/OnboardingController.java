package github.muhsenerdev.users.core.infra.rest;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import github.muhsenerdev.commons.web.config.BaseOpenApiConfig;
import github.muhsenerdev.users.core.application.user.resend_code.CodeResendResponse;
import github.muhsenerdev.users.core.application.user.resend_code.ResendVerificationCodeCommand;
import github.muhsenerdev.users.core.application.user.shared.OnboardingApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/public/onboarding")
@Tag(name = "Registration", description = "User registration endpoint")
@RequiredArgsConstructor
public class OnboardingController {

    private final OnboardingApplicationService service;

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

}