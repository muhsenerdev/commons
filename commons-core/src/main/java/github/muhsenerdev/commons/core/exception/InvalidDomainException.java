package github.muhsenerdev.commons.core.exception;

public class InvalidDomainException extends DomainException {

    public InvalidDomainException(String message) {
        super("invalid.domain", message);
    }

    public InvalidDomainException(String code, String message) {
        super(code, message);
    }
}
