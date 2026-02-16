package github.muhsenerdev.commons.core.exception;

public class DuplicateException extends ApplicationException {
    public DuplicateException(String message) {
        super("duplicate", message);
    }

    public DuplicateException(String code, String message) {
        super(code, message);
    }
}
