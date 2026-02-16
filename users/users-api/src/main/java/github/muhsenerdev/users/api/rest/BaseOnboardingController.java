package github.muhsenerdev.users.api.rest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import github.muhsenerdev.users.api.application.registration.RegisterUserBaseCommand;
import github.muhsenerdev.users.api.application.registration.RegistrationApplicationService;
import github.muhsenerdev.users.api.application.registration.UserRegistrationResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class BaseOnboardingController<T extends RegisterUserBaseCommand> {

    private final RegistrationApplicationService<T> service;

    @PostMapping
    public ResponseEntity<UserRegistrationResponse> register(@RequestBody T command) {
        var response = service.register(command);
        return ResponseEntity.ok(response);
    }

}
