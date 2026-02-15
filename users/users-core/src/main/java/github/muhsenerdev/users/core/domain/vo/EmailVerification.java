package github.muhsenerdev.users.core.domain.vo;

import java.time.OffsetDateTime;

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
                .expiresAt(OffsetDateTime.now().plusHours(24))
                .status(VerificationStatus.VERIFYING)
                .resendCount(0)
                .resendableAt(OffsetDateTime.now())
                .build();
    }
}
