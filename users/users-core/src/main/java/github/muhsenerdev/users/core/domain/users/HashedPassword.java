package github.muhsenerdev.users.core.domain.users;

import github.muhsenerdev.commons.core.exception.InvalidDomainException;
import github.muhsenerdev.commons.core.vo.SingleVO;
import lombok.Builder;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class HashedPassword extends SingleVO<String> {

    private String password;

    private HashedPassword(String value) {
        this.password = value;
        validate(value);

    }

    /**
     * To generate hashed password, call @PasswordService.hash()
     * 
     * @param value Hashed password value
     * @return HashedPassword
     */
    @Builder
    public static HashedPassword of(String value) {
        return new HashedPassword(value);
    }

    private void validate(String value) {
        if (value == null || value.isBlank()) {
            throw new InvalidDomainException("password.invalid", "Hashed password value must not be empty.");
        }
    }

    @Override
    public String getValue() {
        return this.password;
    }

}
