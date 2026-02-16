package github.muhsenerdev.commons.core.exception;

public class AuthenticationRequiredException extends ApplicationException {
    public AuthenticationRequiredException(String message) {
        super("authentication_required", message);
    }

    public AuthenticationRequiredException(String code, String message) {
        super(code, message);
    }
}
