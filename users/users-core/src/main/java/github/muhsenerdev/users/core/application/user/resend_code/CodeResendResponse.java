package github.muhsenerdev.users.core.application.user.resend_code;

import java.time.OffsetDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class CodeResendResponse {

    private OffsetDateTime resendableAt;
    private Integer resendCount;
}
