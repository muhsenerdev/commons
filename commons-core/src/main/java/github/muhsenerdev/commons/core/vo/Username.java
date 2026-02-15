package github.muhsenerdev.commons.core.vo;

import java.util.regex.Pattern;

import github.muhsenerdev.commons.core.exception.InvalidDomainException;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Username extends SingleVO<String> {

    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9._-]{3,}$");

    private String username;

    private Username(String value) {
        validate(value);
    }

    @Builder()
    public static Username of(String value) {
        return new Username(value);
    }

    private void validate(String value) {
        if (value == null || !USERNAME_PATTERN.matcher(value).matches()) {
            throw new InvalidDomainException("username.invalid",
                    "Username must be at least 3 characters and consist of alphanumeric characters, dots, underscores, or hyphens.");
        }
    }

    @Override
    protected String getValue() {
        return this.username;
    }
}
