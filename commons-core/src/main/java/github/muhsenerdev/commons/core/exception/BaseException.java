package github.muhsenerdev.commons.core.exception;

import github.muhsenerdev.commons.core.util.MessageUtils;
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

    protected BaseException(String code, String message, Object... args) {
        super(MessageUtils.format(message, args));
        this.code = code;
    }
}
