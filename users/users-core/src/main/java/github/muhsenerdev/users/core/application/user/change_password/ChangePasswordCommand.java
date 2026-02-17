package github.muhsenerdev.users.core.application.user.change_password;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Builder(toBuilder = true)
@AllArgsConstructor
@Schema(description = "Command for changing user password")
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ChangePasswordCommand {

    @JsonIgnore
    @Schema(hidden = true)
    private UUID userId;

    @Schema(description = "Old password", example = "OldP@ssword123", format = "password")
    private String oldPassword;

    @NotBlank(message = "password.new.required")
    @Schema(description = "New password", example = "NewP@ssword123", requiredMode = Schema.RequiredMode.REQUIRED, format = "password")
    private String newPassword;

    @Builder.Default
    @JsonIgnore
    @Schema(hidden = true)
    private boolean adminChanges = false;

    public ChangePasswordCommand withUserId(UUID userId) {
        return this.toBuilder().userId(userId).build();
    }
}
