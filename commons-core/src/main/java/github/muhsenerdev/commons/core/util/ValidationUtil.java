package github.muhsenerdev.commons.core.util;

import org.apache.commons.validator.routines.EmailValidator;

public final class ValidationUtil {

    private static final EmailValidator EMAIL_VALIDATOR = EmailValidator.getInstance();

    private ValidationUtil() {
    }

    public static boolean isValidEmail(String email) {
        return EMAIL_VALIDATOR.isValid(email);
    }
}
