package github.muhsenerdev.users.core.domain.users;

import java.util.Map;
import java.util.Set;

import github.muhsenerdev.commons.core.exception.InvalidDomainException;
import github.muhsenerdev.users.core.domain.roles.Role;
import lombok.Builder;

public record UserCreationInput(
        String name,
        String username,
        String email,
        String password,
        HashedPassword hashedPassword,
        Set<Role> roles,
        RegistrationType registrationType,
        boolean verified,
        Map<String, Object> metadata,
        Set<String> missingDetails,
        SocialLoginDetails socialLoginDetails) {

    @Builder(toBuilder = true)
    public UserCreationInput(String name, String username, String email, String password, HashedPassword hashedPassword,
            Set<Role> roles, RegistrationType registrationType, boolean verified, Map<String, Object> metadata,
            Set<String> missingDetails, SocialLoginDetails socialLoginDetails) {

        if (registrationType == RegistrationType.PASSWORD && password == null) {
            throw new InvalidDomainException("In user creation input, password is required for password registration");
        }

        if (email == null || email.isBlank()) {
            throw new InvalidDomainException("In user creation input, email is mandatory");
        }

        this.name = name;
        this.username = username;
        this.email = email;
        this.password = password;
        this.roles = roles;
        this.registrationType = registrationType;
        this.verified = verified;
        this.metadata = metadata == null ? Map.of() : metadata;
        this.missingDetails = missingDetails == null ? Set.of() : missingDetails;
        this.socialLoginDetails = socialLoginDetails;
        this.hashedPassword = hashedPassword;
    }

    public boolean isPasswordRegistration() {
        return registrationType == RegistrationType.PASSWORD;
    }

}
