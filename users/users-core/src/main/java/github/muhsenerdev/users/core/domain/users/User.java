package github.muhsenerdev.users.core.domain.users;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.annotations.Type;

import github.muhsenerdev.commons.core.exception.InvalidDomainException;
import github.muhsenerdev.commons.core.vo.Email;
import github.muhsenerdev.commons.core.vo.Name;
import github.muhsenerdev.commons.core.vo.Username;
import github.muhsenerdev.commons.jpa.entity.SoftDeletableEntity;
import github.muhsenerdev.users.core.domain.roles.Role;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@SQLDelete(sql = "UPDATE users SET deleted_at = CURRENT_TIMESTAMP WHERE id = ? and version = ?")
@SQLRestriction("deleted_at IS NULL")

public class User extends SoftDeletableEntity {

    @Embedded
    private Name name;

    @Column(unique = true, nullable = false)
    @Embedded
    private Email email;

    @Column(unique = true)
    @Embedded
    private Username username;

    @Embedded
    private HashedPassword password;

    @Enumerated(EnumType.STRING)
    private UserStatus status;

    @Embedded
    private EmailVerification emailVerification;

    @Embedded
    private PasswordReset passwordReset;

    @Column(columnDefinition = "jsonb")
    @Type(JsonType.class)
    private Map<String, Object> metadata;

    @Enumerated(EnumType.STRING)
    @Column(name = "registration_type")
    private RegistrationType registrationType;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles = new HashSet<>();

    @Column(columnDefinition = "jsonb")
    @Type(JsonType.class)
    private Set<String> missingDetails = new HashSet<>();

    @Column(columnDefinition = "jsonb")
    @Type(JsonType.class)
    private List<SocialLoginDetails> socialLoginDetails;

    @Builder
    public User(Name name, Email email, Username username, HashedPassword password, UserStatus status,
            EmailVerification emailVerification, PasswordReset passwordReset, Map<String, Object> metadata,
            RegistrationType registrationType, Set<Role> roles, Set<String> missingDetails,
            List<SocialLoginDetails> socialLoginDetails) {
        this.name = name;
        this.email = email;
        this.username = username;
        this.password = password;
        this.status = status;
        this.emailVerification = emailVerification;
        this.passwordReset = passwordReset;
        this.metadata = metadata;
        this.registrationType = registrationType;
        this.roles = roles;
        this.missingDetails = missingDetails != null ? missingDetails : new HashSet<>();
        this.socialLoginDetails = socialLoginDetails != null ? socialLoginDetails : new ArrayList<>();

    }

    private void validate() {
        // Ensure has email.
        if (email == null) {
            throw new InvalidDomainException("User must have an email.");
        }

        // Ensure has password if password registration.
        if (this.registrationType == RegistrationType.PASSWORD) {
            if (this.password == null) {
                throw new InvalidDomainException("Password is required for password registration");
            }
        }

        // Ensure has at least one role.
        if (roles == null || roles.isEmpty()) {
            throw new InvalidDomainException("User must have at least one role.");
        }

    }

    public boolean isVerificationExpired() {
        return this.emailVerification.isExpired();
    }

    public boolean isVerifying() {
        return this.emailVerification.isVerifying();
    }

    public void resendVerificationCode() throws InvalidDomainException {
        this.emailVerification = this.emailVerification.resendCode();
        CodeResent codeResent = CodeResent.builder()
                .userId(this.getId())
                .email(this.email.getValue())
                .newCode(this.emailVerification.getCode())
                .expiresAt(this.emailVerification.getExpiresAt())
                .name(this.name != null ? this.name.getValue() : null)
                .build();
        this.registerEvent(codeResent);
    }

    public void verifyEmail(String code) throws InvalidDomainException {
        this.emailVerification.verify(code);
        activateOrIgnore();
    }

    protected void setRoles(Set<Role> newRoles) {
        this.roles = newRoles;
    }

    public static User createSocialUser(UserCreationInput input) {

        var user = User.builder()
                .email(Email.of(input.email()))
                .roles(input.roles())
                .registrationType(RegistrationType.SOCIAL)
                .status(UserStatus.INACTIVE)
                .name(Name.fromStringOrNull(input.name()))
                .emailVerification(EmailVerification.verified())
                .username(Username.fromStringOrNull(input.username()))
                .missingDetails(input.missingDetails())
                .socialLoginDetails(List.of(input.socialLoginDetails()))
                .build();

        user.validate();
        user.activateOrIgnore();
        UserCreated userCreated = UserCreated.builder()
                .userIdSupplier(() -> user.getId())
                .registrationType(RegistrationType.SOCIAL)
                .verified(true)
                .build();
        user.registerEvent(userCreated);

        return user;
    }

    public static User createPasswordUser(UserCreationInput input) {

        var user = User.builder()
                .name(Name.fromStringOrNull(input.name()))
                .username(Username.fromStringOrNull(input.username()))
                .email(Email.of(input.email()))
                .password(input.hashedPassword())
                .roles(input.roles())
                .registrationType(input.registrationType())
                .status(UserStatus.INACTIVE)
                .emailVerification(input.verified() ? EmailVerification.verified() : EmailVerification.create())
                .metadata(input.metadata())
                .build();

        user.validate();
        user.activateOrIgnore();
        UserCreated userCreated = UserCreated.builder()
                .registrationType(RegistrationType.PASSWORD)
                .verified(input.verified())
                .userIdSupplier(() -> user.getId())
                .build();
        user.registerEvent(userCreated);
        return user;
    }

    public boolean hasSocialLogin(SocialProvider provider) {
        if (provider == null) {
            throw new IllegalArgumentException("Provider cannot be null");
        }
        return this.socialLoginDetails.stream()
                .anyMatch(socialLoginDetail -> socialLoginDetail.getProvider().equals(provider));
    }

    public void addNewSocialLogin(SocialLoginDetails socialLoginDetails) {
        if (socialLoginDetails == null) {
            throw new IllegalArgumentException("Social login details cannot be null");
        }

        // IF already logged in with the same provider, ignore it.
        if (hasSocialLogin(socialLoginDetails.getProvider())) {
            return;
        }

        // If user is password based registration and email is verifying, mark as
        // verified.
        if (!isActive() && password != null && emailVerification.isVerifying()) {
            emailVerification.markAsVerified();
            activateOrIgnore();
        }

        // Add social login details.
        this.socialLoginDetails.add(socialLoginDetails);

        // Determine registration type.
        determineRegistrationType();
    }

    public void completeRegistration(Name name, Username username, Map<String, Object> otherDetails) {
        if (name != null) {
            this.name = name;
            this.missingDetails.remove("name");
        }
        if (username != null) {
            this.username = username;
            this.missingDetails.remove("username");
        }

        if (otherDetails != null) {
            if (this.metadata == null) {
                this.metadata = new java.util.HashMap<>();
            }
            for (Map.Entry<String, Object> entry : otherDetails.entrySet()) {
                this.metadata.put(entry.getKey(), entry.getValue());
                this.missingDetails.remove(entry.getKey());
            }
        }

        activateOrIgnore();
    }

    private void activateOrIgnore() {
        if (isActive())
            return;

        if (!emailVerification.isVerified()) {
            return;
        }

        if (missingDetails != null && !missingDetails.isEmpty()) {
            return;
        }

        this.status = UserStatus.ACTIVE;
        this.registerEvent(UserActivatedEvent.builder()
                .userId(getId())
                .userIdSupplier(() -> this.getId())
                .build());
    }

    public void assignNewPassword(HashedPassword newPassword) {
        if (newPassword == null) {
            throw new IllegalArgumentException("New password cannot be null");
        }
        this.password = newPassword;
        determineRegistrationType();
    }

    public boolean isActive() {
        return Objects.equals(this.status, UserStatus.ACTIVE);
    }

    public void determineRegistrationType() {
        boolean isPassword = password != null;
        boolean isSocial = !socialLoginDetails.isEmpty();

        if (isPassword && isSocial) {
            this.registrationType = RegistrationType.HYBRID;
            return;
        }

        if (isPassword) {
            this.registrationType = RegistrationType.PASSWORD;
            return;
        }

        if (isSocial) {
            this.registrationType = RegistrationType.SOCIAL;
            return;
        }

    }
}
