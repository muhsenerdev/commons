package github.muhsenerdev.commons.core.exception;

public class NotFoundException extends ApplicationException {
    public NotFoundException(String message) {
        super("not_found", message);
    }

    public NotFoundException(String code, String message) {
        super(code, message);
    }
}
