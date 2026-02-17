package github.muhsenerdev.users.core.domain.users;

import github.muhsenerdev.commons.core.exception.InvalidDomainException;
import lombok.Getter;

@Getter
public enum RegistrationType {
    PASSWORD,
    SOCIAL,
    HYBRID;

    public static RegistrationType fromString(String value) {
        RegistrationType type = fromStringOrNull(value);
        if (type == null) {
            throw new InvalidDomainException("registration.type.invalid", "Invalid registration type: " + value);
        }
        return type;
    }

    public static RegistrationType fromStringOrNull(String value) {
        if (value == null) {
            return null;
        }
        for (RegistrationType type : RegistrationType.values()) {
            if (type.name().equalsIgnoreCase(value)) {
                return type;
            }
        }
        return null;
    }
}
