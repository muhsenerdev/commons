package github.muhsenerdev.users.core.application.user.resend_code;

import java.time.OffsetDateTime;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CodeResendResponse {

    @Schema(description = "The time when the code can be resent", example = "2022-01-01T00:00:00Z")
    private OffsetDateTime resendableAt;

    @Schema(description = "The number of times the code has been resent", example = "0")
    private Integer resendCount;
}
