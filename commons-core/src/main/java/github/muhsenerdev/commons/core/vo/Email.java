package github.muhsenerdev.commons.core.vo;

import github.muhsenerdev.commons.core.exception.InvalidDomainException;
import github.muhsenerdev.commons.core.util.ValidationUtil;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Email extends SingleVO<String> {

    private String email;

    private Email(String value) {
        this.email = value;
        validate(value);
    }

    @Builder()
    public static Email of(String value) {
        return new Email(value);
    }

    private void validate(String value) {
        if (value == null || !ValidationUtil.isValidEmail(value)) {
            throw new InvalidDomainException("email.invalid", "Invalid email format.");
        }
    }

    @Override
    public String getValue() {
        return this.email;
    }
}
