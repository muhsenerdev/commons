package github.muhsenerdev.users.core.domain.users;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.instancio.Select.field;

import java.time.OffsetDateTime;

import org.instancio.Instancio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import github.muhsenerdev.commons.core.exception.InvalidDomainException;

public class EmailVerificationTest {

    private EmailVerification verification;

    @BeforeEach
    void setUp() {
        verification = EmailVerification.create();
    }

    @Test
    @DisplayName("Given newly created verification, when resend code, then throw exception")
    void givenNewlyCreatedVerification_whenResendCode_thenThrowException() {
        assertThatThrownBy(() -> verification.resendCode())
                .isInstanceOf(InvalidDomainException.class)
                .hasMessage("Please wait before resending the code.");
    }

    @Test
    @DisplayName("Given resend limit reached verification, when resend code, then throw exception")
    void givenResendLimitReachedVerification_whenResendCode_thenThrowException() {

        verification = Instancio.of(EmailVerification.class)
                .set(field(EmailVerification::getResendCount), EmailVerification.MAX_RESEND_COUNT)
                .set(field(EmailVerification::getStatus), VerificationStatus.VERIFYING)
                .set(field(EmailVerification::getResendableAt), OffsetDateTime.now().minusHours(1))
                .create();

        assertThatThrownBy(() -> verification.resendCode())
                .isInstanceOf(InvalidDomainException.class)

                .hasMessageContaining("Maximum resend limit");
    }

    @Test
    @DisplayName("Given not verifying verification, when resend code, then throws exception")
    void givenNotVerifyingVerification_whenResendCode_thenThrowsException() {
        verification = Instancio.of(EmailVerification.class)
                .set(field(EmailVerification::getStatus), VerificationStatus.VERIFIED)
                .create();

        assertThatThrownBy(() -> verification.resendCode())
                .isInstanceOf(InvalidDomainException.class)
                .hasMessageContaining("not in verifying status");
    }

    @Test
    @DisplayName("Given resendable verification, when resend code, then resend code successfully")
    void givenResendableVerification_thenResendCodeSuccessfully() {
        String oldCode = verification.getCode();
        ReflectionTestUtils.setField(verification, "resendableAt", OffsetDateTime.now().minusSeconds(3));

        verification = verification.resendCode();

        assertThat(verification.getCode()).isNotEqualTo(oldCode);
        assertThat(verification.getExpiresAt()).isAfter(OffsetDateTime.now());
        assertThat(verification.getStatus()).isEqualTo(VerificationStatus.VERIFYING);
        assertThat(verification.getResendCount()).isEqualTo(1);
        assertThat(verification.getResendableAt()).isAfter(OffsetDateTime.now());
    }

    @Nested
    @DisplayName("EmailVerification.verify() Tests")
    class VerifyTests {

        @Test
        @DisplayName("Given not verifying verification, when verify, then throws exception")
        void givenNotVerifyingVerification_whenVerify_thenThrowsException() {
            verification = Instancio.of(EmailVerification.class)
                    .set(field(EmailVerification::getStatus), VerificationStatus.VERIFIED)
                    .create();

            assertThatThrownBy(() -> verification.verify("123456"))
                    .isInstanceOf(InvalidDomainException.class)
                    .hasMessageContaining("not in verifying status");
        }

        @Test
        @DisplayName("Given expired verification, when verify, then throws exception")
        void givenExpiredVerification_whenVerify_thenThrowsException() {
            verification = Instancio.of(EmailVerification.class)
                    .set(field(EmailVerification::getStatus), VerificationStatus.VERIFYING)
                    .set(field(EmailVerification::getExpiresAt), OffsetDateTime.now().minusSeconds(3))
                    .create();

            assertThatThrownBy(() -> verification.verify(verification.getCode()))
                    .isInstanceOf(InvalidDomainException.class)
                    .hasMessageContaining("expired");
        }

        @Test
        @DisplayName("Given invalid code verification, when verify, then throws exception")
        void givenInvalidCodeVerification_whenVerify_thenThrowsException() {
            verification = Instancio.of(EmailVerification.class)
                    .set(field(EmailVerification::getStatus), VerificationStatus.VERIFYING)
                    .create();

            assertThatThrownBy(() -> verification.verify("123456"))
                    .isInstanceOf(InvalidDomainException.class)
                    .hasMessageContaining("Invalid verification code");
        }

        @Test
        @DisplayName("Given valid verification, when verify, then verify successfully")
        void givenValidVerification_whenVerify_thenVerifySuccessfully() {
            verification = EmailVerification.create();

            verification.verify(verification.getCode());

            assertThat(verification.getStatus()).isEqualTo(VerificationStatus.VERIFIED);
        }
    }
}
