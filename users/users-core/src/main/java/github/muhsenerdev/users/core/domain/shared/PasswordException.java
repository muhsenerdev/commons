package github.muhsenerdev.users.core.domain.shared;

import github.muhsenerdev.commons.core.exception.DomainException;

public class PasswordException extends DomainException {

    public PasswordException(String message) {
        super("password.invalid", message);
    }

    public PasswordException(String code, String message) {
        super(code, message);
    }

}
