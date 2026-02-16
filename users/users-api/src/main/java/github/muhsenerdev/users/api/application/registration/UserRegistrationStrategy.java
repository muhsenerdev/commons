package github.muhsenerdev.users.api.application.registration;

public interface UserRegistrationStrategy<T extends RegisterUserBaseCommand> {

    void validate(T command);

}
