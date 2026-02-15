package github.muhsenerdev.users.core.domain.vo;

import github.muhsenerdev.commons.core.exception.InvalidDomainException;
import lombok.Getter;

@Getter
public enum PasswordResetStatus {
    PENDING,
    COMPLETED,
    EXPIRED;

    public static PasswordResetStatus fromString(String value) {
        PasswordResetStatus status = fromStringOrNull(value);
        if (status == null) {
            throw new InvalidDomainException("password.reset.status.invalid",
                    "Invalid password reset status: " + value);
        }
        return status;
    }

    public static PasswordResetStatus fromStringOrNull(String value) {
        if (value == null) {
            return null;
        }
        for (PasswordResetStatus status : PasswordResetStatus.values()) {
            if (status.name().equalsIgnoreCase(value)) {
                return status;
            }
        }
        return null;
    }
}
