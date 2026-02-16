package github.muhsenerdev.users.core.infra.adapter.rest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import github.muhsenerdev.users.api.application.registration.RegistrationApplicationService;
import github.muhsenerdev.users.api.rest.BaseRegistrationController;
import github.muhsenerdev.users.core.application.user.register.DefaultRegistrationCommand;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1/public/onboarding")
@Tag(name = "Registration", description = "User registration endpoint")
public class DefaultRegistrationController extends BaseRegistrationController<DefaultRegistrationCommand> {

    public DefaultRegistrationController(RegistrationApplicationService<DefaultRegistrationCommand> service) {
        super(service);

    }

}
