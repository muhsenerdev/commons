package github.muhsenerdev.users.core.domain.users;

import github.muhsenerdev.commons.core.exception.InvalidDomainException;
import lombok.Getter;

@Getter
public enum UserStatus {
    INACTIVE,
    ACTIVE,
    BANNED;

    public static UserStatus fromString(String value) {
        UserStatus status = fromStringOrNull(value);
        if (status == null) {
            throw new InvalidDomainException("user.status.invalid", "Invalid user status: " + value);
        }
        return status;
    }

    public static UserStatus fromStringOrNull(String value) {
        if (value == null) {
            return null;
        }
        for (UserStatus status : UserStatus.values()) {
            if (status.name().equalsIgnoreCase(value)) {
                return status;
            }
        }
        return null;
    }
}
