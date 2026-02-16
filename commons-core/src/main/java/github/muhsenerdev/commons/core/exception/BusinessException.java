package github.muhsenerdev.commons.core.exception;

public class BusinessException extends ApplicationException {
    public BusinessException(String message) {
        super("business_error", message);
    }

    public BusinessException(String code, String message) {
        super(code, message);
    }
}
