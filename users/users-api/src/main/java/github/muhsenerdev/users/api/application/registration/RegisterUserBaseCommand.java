package github.muhsenerdev.users.api.application.registration;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Getter
@NoArgsConstructor
@SuperBuilder
@Schema(description = "Base command for user registration")
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public abstract class RegisterUserBaseCommand {

    @NotBlank(message = "registration.email.required")
    @Email(message = "registration.email.invalid")
    @Schema(description = "User email address", example = "user@example.com", requiredMode = Schema.RequiredMode.REQUIRED)
    protected String email;

    @NotBlank(message = "registration.password.required")
    @Schema(description = "User password", example = "P@ssword123", requiredMode = Schema.RequiredMode.REQUIRED, format = "password")
    protected String password;

    @Schema(description = "User full name", example = "John Doe")
    protected String name;

    @Schema(description = "User unique username", example = "johndoe")
    protected String username;

}
