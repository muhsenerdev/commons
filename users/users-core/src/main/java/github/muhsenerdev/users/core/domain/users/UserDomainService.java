package github.muhsenerdev.users.core.domain.users;

import org.springframework.stereotype.Service;

import github.muhsenerdev.commons.core.exception.InvalidDomainException;
import github.muhsenerdev.commons.core.vo.Email;
import github.muhsenerdev.commons.core.vo.Name;
import github.muhsenerdev.commons.core.vo.Username;
import github.muhsenerdev.users.core.domain.shared.PasswordService;
import github.muhsenerdev.users.core.infra.config.UserModuleProperties;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserDomainService {

    private final PasswordService passwordService;
    private final UserRepository userRepository;
    private final UserModuleProperties properties;

    public User createUser(UserCreationInput input) {

        var registrationType = input.registrationType();

        switch (registrationType) {
            case PASSWORD -> {
                return handlePasswordRegistration(input);
            }
            default -> throw new IllegalStateException("Unsupported registration type: " + registrationType);
        }

    }

    private User handlePasswordRegistration(UserCreationInput input) {
        assert input.isPasswordRegistration();

        var hashedPassword = passwordService.hash(input.password());

        // Create name if required.
        Name name = null;
        if (properties.isNameRequired()) {
            name = Name.of(input.name());
        }

        // Check username is required and then determine final username.
        Username username = null;
        if (properties.isUsernameRequired()) {
            username = Username.of(input.username());

            if (userRepository.existsByUsername(username)) {
                throw new InvalidDomainException("Username is already taken: " + input.username());
            }

        }

        // Check email uniquness.
        Email email = Email.of(input.email());
        if (userRepository.existsByEmail(email)) {
            throw new InvalidDomainException("Email is already taken: " + input.email());
        }

        var user = User.createPasswordUser(input.toBuilder().hashedPassword(hashedPassword).build());

        return user;
    }

    public void changePassword(User user, String newPassword, String oldPassword, boolean adminChanges) {
        if (adminChanges) {
            passwordService.validate(newPassword);
            var hashedPassword = passwordService.hash(newPassword);
            user.assignNewPassword(hashedPassword);
        } else {
            // User self change password.
            // 1. Check registration type.
            var type = user.getRegistrationType();
            if (type != RegistrationType.PASSWORD && type != RegistrationType.HYBRID) {
                throw new InvalidDomainException("User is not a password-enabled user.");
            }

            // 2. Check old password.
            if (!passwordService.matches(oldPassword, user.getPassword())) {
                throw new InvalidDomainException("Old password does not match.");
            }

            // 3. Validate and hash new password.
            passwordService.validate(newPassword);
            var hashedPassword = passwordService.hash(newPassword);

            // 4. Assign new password.
            user.assignNewPassword(hashedPassword);
        }
    }

}
