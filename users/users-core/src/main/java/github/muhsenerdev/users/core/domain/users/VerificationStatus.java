package github.muhsenerdev.users.core.domain.users;

import github.muhsenerdev.commons.core.exception.InvalidDomainException;
import lombok.Getter;

@Getter
public enum VerificationStatus {
    VERIFYING,
    VERIFIED,
    EXPIRED;

    public static VerificationStatus fromString(String value) {
        VerificationStatus status = fromStringOrNull(value);
        if (status == null) {
            throw new InvalidDomainException("verification.status.invalid", "Invalid verification status: " + value);
        }
        return status;
    }

    public static VerificationStatus fromStringOrNull(String value) {
        if (value == null) {
            return null;
        }
        for (VerificationStatus status : VerificationStatus.values()) {
            if (status.name().equalsIgnoreCase(value)) {
                return status;
            }
        }
        return null;
    }
}
