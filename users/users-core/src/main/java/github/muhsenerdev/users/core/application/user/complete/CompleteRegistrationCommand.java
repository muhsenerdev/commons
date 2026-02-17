package github.muhsenerdev.users.core.application.user.complete;

import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(toBuilder = true)
@Schema(description = "Command to complete user registration by providing missing details")
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CompleteRegistrationCommand {

    @Schema(hidden = true)
    @NotNull(message = "User id is required to complete registration.")
    private UUID userId;

    @Schema(description = "User full name", example = "John Doe")
    private final String name;

    @Schema(description = "User unique username", example = "johndoe")
    private final String username;

    @Schema(description = "Other module-specific details", example = "{\"phone\": \"+90555...\", \"birth_date\": \"1990-01-01\"}")
    private final Map<String, Object> otherDetails;

}
