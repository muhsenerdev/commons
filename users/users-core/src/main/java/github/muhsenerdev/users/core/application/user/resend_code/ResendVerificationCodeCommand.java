package github.muhsenerdev.users.core.application.user.resend_code;

import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ResendVerificationCodeCommand {
    @NotNull(message = "resend_code.user_id.required")
    @Schema(description = "User ID", example = "123e4567-e89b-12d3-a456-426614174000", requiredMode = Schema.RequiredMode.REQUIRED)
    private UUID userId;
}
