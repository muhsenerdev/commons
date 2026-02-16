package github.muhsenerdev.commons.core.vo;

import github.muhsenerdev.commons.core.exception.InvalidDomainException;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Name extends SingleVO<String> {

    private String name;

    private Name(String value) {
        validate(value);
        this.name = value;
    }

    @Builder
    public static Name of(String value) {
        return new Name(value);
    }

    private void validate(String value) {
        if (value == null || value.trim().length() < 3 || value.trim().length() > 100) {
            throw new InvalidDomainException("name.invalid", "Name must be between 3 and 100 characters.");
        }
    }

    @Override
    public String getValue() {
        return this.name;
    }
}
