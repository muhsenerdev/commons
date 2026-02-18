package github.muhsenerdev.users.core.application.user.list;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import github.muhsenerdev.users.core.domain.users.RegistrationType;
import github.muhsenerdev.users.core.domain.users.UserStatus;
import github.muhsenerdev.users.core.domain.users.VerificationStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@Schema(description = "Detailed user information for listing")
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UserListItem {
    private final UUID id;
    private final String name;
    private final String email;
    private final String username;
    private final UserStatus status;
    private final VerificationStatus verificationStatus;
    private final RegistrationType registrationType;
    private final Map<String, Object> metadata;
    private final Set<RoleDTO> roles;
    private final Set<String> missingDetails;
    private final OffsetDateTime createdAt;
    private final OffsetDateTime updatedAt;

    @Getter
    @Builder
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class RoleDTO {
        private final UUID id;
        private final String name;
    }
}
