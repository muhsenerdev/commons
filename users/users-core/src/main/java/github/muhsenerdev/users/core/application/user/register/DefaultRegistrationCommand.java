package github.muhsenerdev.users.core.application.user.register;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import github.muhsenerdev.users.api.application.registration.RegisterUserBaseCommand;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
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
