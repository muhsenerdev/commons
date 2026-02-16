package github.muhsenerdev.users.core.domain.users;

import java.util.HashSet;
import java.util.Map;
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

    @Builder
    public User(Name name, Email email, Username username, HashedPassword password, UserStatus status,
            EmailVerification emailVerification, PasswordReset passwordReset, Map<String, Object> metadata,
            RegistrationType registrationType, Set<Role> roles) {
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
    }

    public static User createPasswordUser(Name name, Username username, Email email, HashedPassword password,
            Set<Role> roles, boolean verified) {

        EmailVerification verification = EmailVerification.create();
        UserStatus status = UserStatus.INACTIVE;

        if (verified) {
            verification = EmailVerification.verified();
            status = UserStatus.ACTIVE;
        }

        var user = User.builder()
                .name(name)
                .username(username)
                .email(email)
                .password(password)
                .roles(roles)
                .registrationType(RegistrationType.PASSWORD)
                .status(status)
                .emailVerification(verification)
                .build();

        user.validate();
        return user;
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

    public static User createSocialUser(Email email, Set<Role> roles, UserStatus status) {
        EmailVerification verification = EmailVerification.verified();

        var user = User.builder()
                .email(email)
                .roles(roles)
                .registrationType(RegistrationType.SOCIAL)
                .status(status)
                .emailVerification(verification)
                .build();

        return user;
    }

    public boolean isVerificationExpired() {
        return this.emailVerification.isExpired();
    }

    public boolean isVerifying() {
        return this.emailVerification.isVerifying();
    }

    public void resendVerificationCode() {
        this.emailVerification = this.emailVerification.resendCode();
    }

    protected void setRoles(Set<Role> newRoles) {
        this.roles = newRoles;
    }

}
