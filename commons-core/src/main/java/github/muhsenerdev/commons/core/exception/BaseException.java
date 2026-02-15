package github.muhsenerdev.commons.core.exception;

import lombok.Getter;

@Getter
public abstract class BaseException extends RuntimeException {

    private String code = "unknown";

    protected BaseException(String message) {
        super(message);
    }

    protected BaseException(String code, String message) {
        super(message);
        this.code = code;
    }
}
