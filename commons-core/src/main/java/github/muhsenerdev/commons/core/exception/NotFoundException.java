package github.muhsenerdev.commons.core.exception;

public class NotFoundException extends ApplicationException {
    public NotFoundException(String message) {
        super("not_found", message);
    }

    public NotFoundException(String code, String message) {
        super(code, message);
    }

    public NotFoundException(String entity, String field, Object value) {
        super(entity.toLowerCase() + ".not_found",
                String.format("%s not found with %s: [%s]", entity, field, value));
    }
}
