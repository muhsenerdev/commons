package github.muhsenerdev.users.core.domain.shared;

import github.muhsenerdev.users.core.domain.users.HashedPassword;

public interface PasswordService {

    HashedPassword hash(String rawPassworrd);

    void validate(String rawPassworrd) throws PasswordException;

    boolean matches(String rawPassword, HashedPassword hashedPassword);
}
