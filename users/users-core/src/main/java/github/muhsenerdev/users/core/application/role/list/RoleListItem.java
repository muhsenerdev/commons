package github.muhsenerdev.users.core.application.role.list;

import java.util.UUID;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "Information about a role in the system")
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class RoleListItem {
    @Schema(description = "Unique identifier of the role", example = "550e8400-e29b-41d4-a716-446655440000")
    private final UUID id;

    @Schema(description = "Name of the role", example = "ROLE_USER")
    private final String name;
}
