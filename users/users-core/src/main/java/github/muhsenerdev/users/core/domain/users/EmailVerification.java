package github.muhsenerdev.users.core.domain.users;

import java.time.Duration;
import java.time.OffsetDateTime;

import github.muhsenerdev.commons.core.exception.InvalidDomainException;
import github.muhsenerdev.commons.core.util.RandomUtil;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Embeddable
@Getter
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EmailVerification {

    private static final Duration EXPIRATION_DURATION = Duration.ofHours(24);
    private static final Duration FIRST_RESEND_COOLDOWN_DURATION = Duration.ofSeconds(30);

    public static final int MAX_RESEND_COUNT = 4;

    @Column(name = "verification_code")
    private String code;

    @Column(name = "verification_expires_at")
    private OffsetDateTime expiresAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "verification_status")
    private VerificationStatus status;

    @Column(name = "resendable_at")
    private OffsetDateTime resendableAt;

    @Column(name = "resend_count")
    private Integer resendCount;

    public static EmailVerification create() {
        return EmailVerification.builder()
                .code(RandomUtil.generateRandomCode(6))
                .expiresAt(OffsetDateTime.now().plus(EXPIRATION_DURATION))
                .status(VerificationStatus.VERIFYING)
                .resendCount(0)
                .resendableAt(OffsetDateTime.now().plus(FIRST_RESEND_COOLDOWN_DURATION))
                .build();
    }

    public static EmailVerification verified() {
        return EmailVerification.builder()
                .status(VerificationStatus.VERIFIED)
                .build();
    }

    public void verify(String code) throws InvalidDomainException {
        // Check it is verifying.
        if (this.status != VerificationStatus.VERIFYING) {
            throw new InvalidDomainException("registration.verification.not-verifying",
                    "User is not in verifying status.");
        }

        // Check code is correct.
        if (!this.code.equals(code)) {
            throw new InvalidDomainException("registration.verification.invalid-code",
                    "Invalid verification code.");
        }

        // Check it is not expired.
        if (this.expiresAt.isBefore(OffsetDateTime.now())) {
            throw new InvalidDomainException("registration.verification.expired",
                    "Verification code has expired.");
        }

        this.status = VerificationStatus.VERIFIED;

    }

    public EmailVerification resendCode() {
        if (this.status != VerificationStatus.VERIFYING) {
            throw new InvalidDomainException("registration.verification.not-verifying",
                    "User is not in verifying status.");
        }

        if (this.resendableAt.isAfter(OffsetDateTime.now())) {
            throw new InvalidDomainException("registration.verification.cooldown-active",
                    "Please wait before resending the code.");
        }

        if (this.resendCount >= 4) {
            throw new InvalidDomainException("registration.verification.resend-limit-exceeded",
                    "Maximum resend limit reached.");
        }

        Duration cooldown = switch (this.resendCount) {
            case 0 -> Duration.ofSeconds(45);
            case 1 -> Duration.ofMinutes(1);
            case 2 -> Duration.ofMinutes(1).plusSeconds(30);
            case 3 -> Duration.ofMinutes(2);
            default -> throw new InvalidDomainException("registration.verification.resend-limit-exceeded",
                    "Maximum resend limit reached.");
        };

        return EmailVerification.builder()
                .code(RandomUtil.generateRandomCode(6))
                .expiresAt(OffsetDateTime.now().plus(EXPIRATION_DURATION))
                .status(VerificationStatus.VERIFYING)
                .resendCount(this.resendCount + 1)
                .resendableAt(OffsetDateTime.now().plus(cooldown))
                .build();
    }

    public boolean isExpired() {
        return this.status == VerificationStatus.EXPIRED || this.expiresAt.isBefore(OffsetDateTime.now());
    }

    public boolean isVerifying() {
        return this.status == VerificationStatus.VERIFYING;
    }
}
