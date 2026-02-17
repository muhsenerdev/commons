package github.muhsenerdev.users.core.application.user.register;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Builder(toBuilder = true)
@AllArgsConstructor
@Schema(description = "Command for user registration")
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class RegisterUserCommand {

    @NotBlank(message = "registration.email.required")
    @Email(message = "registration.email.invalid")
    @Schema(description = "User email address", example = "user@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;

    @NotBlank(message = "registration.password.required")
    @Schema(description = "User password", example = "P@ssword123", requiredMode = Schema.RequiredMode.REQUIRED, format = "password")
    private String password;

    @Schema(description = "User full name", example = "John Doe")
    private String name;

    @Schema(description = "User unique username", example = "johndoe")
    private String username;

    @Builder.Default
    private Map<String, Object> otherDetails = new HashMap<>();

    public RegisterUserCommand withEmail(String value) {
        return this.toBuilder().email(value).build();
    }

    public RegisterUserCommand withUsername(String value) {
        return this.toBuilder().username(value).build();
    }

}
