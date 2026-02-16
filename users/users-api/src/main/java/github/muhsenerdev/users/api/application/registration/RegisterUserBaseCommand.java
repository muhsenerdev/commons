package github.muhsenerdev.users.api.application.registration;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@NoArgsConstructor
@SuperBuilder
public abstract class RegisterUserBaseCommand {
    protected String email;
    protected String password;
    protected String name;
    protected String username;

}
