package github.muhsenerdev.commons.core.exception;

public class InvalidInputException extends DomainException {

    public InvalidInputException(String message) {
        this("invalid_input", message);
    }

    public InvalidInputException(String code, String message) {
        super(code, message);
    }

    public InvalidInputException(String code, String message, Object... args) {
        super(code, message, args);
    }
}
