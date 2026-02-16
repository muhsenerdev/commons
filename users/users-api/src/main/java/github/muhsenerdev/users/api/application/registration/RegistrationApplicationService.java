package github.muhsenerdev.users.api.application.registration;

public interface RegistrationApplicationService<T extends RegisterUserBaseCommand> {

    UserRegistrationResponse register(T command);

}
