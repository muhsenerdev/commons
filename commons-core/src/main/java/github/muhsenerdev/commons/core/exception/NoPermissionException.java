package github.muhsenerdev.commons.core.exception;

public class NoPermissionException extends ApplicationException {
    public NoPermissionException(String message) {
        super("no_permission", message);
    }

    public NoPermissionException(String code, String message) {
        super(code, message);
    }
}
