package github.muhsenerdev.commons.core.exception;

public abstract class DomainException extends BaseException {

    public DomainException(String message) {
        super(message);
    }

    public DomainException(String code, String message) {
        super(code, message);
    }
}
