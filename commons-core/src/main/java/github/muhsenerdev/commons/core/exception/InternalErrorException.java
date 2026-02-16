package github.muhsenerdev.commons.core.exception;

public class InternalErrorException extends ApplicationException {
    public InternalErrorException(String message) {
        super("internal_error", message);
    }

    public InternalErrorException(String code, String message) {
        super(code, message);
    }
}
