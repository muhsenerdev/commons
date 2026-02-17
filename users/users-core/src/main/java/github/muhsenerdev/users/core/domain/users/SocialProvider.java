package github.muhsenerdev.users.core.domain.users;

import github.muhsenerdev.commons.core.exception.InvalidDomainException;

public enum SocialProvider {
    GOOGLE;

    public static SocialProvider fromString(String value) {
        SocialProvider status = fromStringOrNull(value);
        if (status == null) {
            throw new InvalidDomainException("social.provider.invalid", "Invalid social provider: " + value);
        }
        return status;
    }

    public static SocialProvider fromStringOrNull(String value) {
        if (value == null) {
            return null;
        }
        for (SocialProvider status : SocialProvider.values()) {
            if (status.name().equalsIgnoreCase(value)) {
                return status;
            }
        }
        return null;
    }

}
