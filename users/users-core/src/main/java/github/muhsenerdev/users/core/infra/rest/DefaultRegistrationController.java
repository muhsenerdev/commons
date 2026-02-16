package github.muhsenerdev.users.core.infra.rest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import github.muhsenerdev.users.api.application.registration.RegistrationApplicationService;
import github.muhsenerdev.users.api.rest.BaseOnboardingController;
import github.muhsenerdev.users.core.application.user.register.DefaultRegistrationCommand;

@RestController
@RequestMapping("/api/v1/public/onboarding/registration")
public class DefaultRegistrationController extends BaseOnboardingController<DefaultRegistrationCommand> {

    public DefaultRegistrationController(RegistrationApplicationService<DefaultRegistrationCommand> service) {
        super(service);
    }

}
