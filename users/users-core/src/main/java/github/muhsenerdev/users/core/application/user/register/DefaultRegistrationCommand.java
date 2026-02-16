package github.muhsenerdev.users.core.application.user.register;

import github.muhsenerdev.users.api.application.registration.RegisterUserBaseCommand;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class DefaultRegistrationCommand extends RegisterUserBaseCommand {

    public DefaultRegistrationCommand withEmail(String email) {
        this.email = email;
        return this;

    }

    public DefaultRegistrationCommand withPassword(String password) {
        this.password = password;
        return this;
    }

    public DefaultRegistrationCommand withUsername(String username) {
        this.username = username;
        return this;
    }

    public DefaultRegistrationCommand withName(String name) {
        this.name = name;
        return this;
    }

}
