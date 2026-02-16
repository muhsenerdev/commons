package github.muhsenerdev.commons.core.exception;

public abstract class ApplicationException extends BaseException {
    protected ApplicationException(String message) {
        super(message);
    }

    protected ApplicationException(String code, String message) {
        super(code, message);
    }
}
