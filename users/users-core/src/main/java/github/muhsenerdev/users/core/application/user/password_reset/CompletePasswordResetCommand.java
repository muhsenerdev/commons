package github.muhsenerdev.users.core.application.user.password_reset;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Command for completing password reset")
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CompletePasswordResetCommand {

    @NotBlank(message = "email.required")
    @Email(message = "email.invalid")
    @Schema(description = "Email of the user", example = "user@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;

    @NotBlank(message = "code.required")
    @Schema(description = "Reset code sent to email", example = "123456", requiredMode = Schema.RequiredMode.REQUIRED)
    private String code;

    @NotBlank(message = "password.new.required")
    @Schema(description = "New password", example = "NewP@ssword123", requiredMode = Schema.RequiredMode.REQUIRED, format = "password")
    private String newPassword;
}
