package github.muhsenerdev.users.core.infra.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import github.muhsenerdev.commons.web.config.BaseOpenApiConfig;
import github.muhsenerdev.users.api.application.registration.RegistrationApplicationService;
import github.muhsenerdev.users.api.application.registration.UserRegistrationResponse;
import github.muhsenerdev.users.api.rest.BaseOnboardingController;
import github.muhsenerdev.users.core.application.user.register.DefaultRegistrationCommand;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/public/onboarding/registration")
@Tag(name = "Registration", description = "User registration endpoint")
public class DefaultRegistrationController extends BaseOnboardingController<DefaultRegistrationCommand> {

    public DefaultRegistrationController(RegistrationApplicationService<DefaultRegistrationCommand> service) {
        super(service);
    }

    @Override
    @PostMapping
    @Operation(summary = "Register a new user", description = "Creates a new user account with the provided details. Returns the registration ID.", responses = {
            @ApiResponse(responseCode = "200", description = "User successfully registered"),
            @ApiResponse(responseCode = "400", ref = BaseOpenApiConfig.BAD_REQUEST_RESPONSE),
            @ApiResponse(responseCode = "500", ref = BaseOpenApiConfig.INTERNAL_SERVER_ERROR_RESPONSE)
    })
    public ResponseEntity<UserRegistrationResponse> register(@Valid @RequestBody DefaultRegistrationCommand command) {
        return super.register(command);
    }

}
