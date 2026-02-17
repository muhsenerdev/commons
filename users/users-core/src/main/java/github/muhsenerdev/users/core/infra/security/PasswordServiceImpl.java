package github.muhsenerdev.users.core.infra.security;

import org.passay.PasswordData;
import org.passay.PasswordValidator;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import github.muhsenerdev.users.core.domain.shared.PasswordException;
import github.muhsenerdev.users.core.domain.shared.PasswordService;
import github.muhsenerdev.users.core.domain.users.HashedPassword;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PasswordServiceImpl implements PasswordService {

    private final PasswordEncoder passwordEncoder;
    private final PasswordValidator passwordValidator;

    @Override
    public HashedPassword hash(String rawPassworrd) {
        checkInput(rawPassworrd);
        return HashedPassword.of(passwordEncoder.encode(rawPassworrd));
    }

    private void checkInput(String input) {
        if (input == null || input.isBlank()) {
            throw new IllegalArgumentException("rawPassword cannot be null or empty");
        }
    }

    private void checkInput(HashedPassword input) {
        if (input == null) {
            throw new IllegalArgumentException("hashedPassword cannot be null");
        }
    }

    @Override
    public void validate(String rawPassworrd) throws PasswordException {
        checkInput(rawPassworrd);
        var passwordData = new PasswordData(rawPassworrd);
        var validationResult = passwordValidator.validate(passwordData);
        if (validationResult.isValid()) {
            return;
        }
        throw new PasswordException("Invalid password");
    }

    @Override
    public boolean matches(String rawPassword, HashedPassword hashedPassword) {
        checkInput(rawPassword);
        checkInput(hashedPassword);
        return passwordEncoder.matches(rawPassword, hashedPassword.getValue());
    }

}
