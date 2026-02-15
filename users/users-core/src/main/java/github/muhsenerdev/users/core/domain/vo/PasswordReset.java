package github.muhsenerdev.users.core.domain.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;

import java.time.OffsetDateTime;

import github.muhsenerdev.commons.core.util.RandomUtil;

@Embeddable
@Getter
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PasswordReset {

    @Column(name = "reset_code")
    private String code;

    @Column(name = "reset_code_expires_at")
    private OffsetDateTime expiresAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "reset_status")
    private PasswordResetStatus status;

    public static PasswordReset create() {
        return PasswordReset.builder()
                .code(RandomUtil.generateRandomCode(6))
                .expiresAt(OffsetDateTime.now().plusHours(2))
                .status(PasswordResetStatus.PENDING)
                .build();
    }
}
