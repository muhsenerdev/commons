package github.muhsenerdev.users.api.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import github.muhsenerdev.commons.web.config.BaseOpenApiConfig;
import github.muhsenerdev.users.api.application.registration.RegisterUserBaseCommand;
import github.muhsenerdev.users.api.application.registration.RegistrationApplicationService;
import github.muhsenerdev.users.api.application.registration.UserRegistrationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class BaseRegistrationController<T extends RegisterUserBaseCommand> {

    private final RegistrationApplicationService<T> service;

    @PostMapping("/registration")
    @Operation(summary = "Register a new user", description = "Creates a new user account with the provided details. Returns the registration ID.", responses = {
            @ApiResponse(responseCode = "200", description = "User successfully registered"),
            @ApiResponse(responseCode = "400", ref = BaseOpenApiConfig.BAD_REQUEST_RESPONSE),
            @ApiResponse(responseCode = "500", ref = BaseOpenApiConfig.INTERNAL_SERVER_ERROR_RESPONSE)
    })
    public ResponseEntity<UserRegistrationResponse> register(@Valid @RequestBody T command) {
        return ResponseEntity.ok(service.register(command));
    }

}
