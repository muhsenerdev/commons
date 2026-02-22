package github.muhsenerdev.commons.core.util;

import github.muhsenerdev.commons.core.exception.InvalidInputException;

public class AssertUtil {

    public static void notNull(Object object, String message) {
        if (object == null) {
            throw InvalidInputException.of(message);
        }
    }
}
