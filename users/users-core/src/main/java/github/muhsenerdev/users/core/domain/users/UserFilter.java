package github.muhsenerdev.users.core.domain.users;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class UserFilter {
    UserStatus status;
    VerificationStatus verificationStatus;
    String email;
    RegistrationType registrationType;
}
