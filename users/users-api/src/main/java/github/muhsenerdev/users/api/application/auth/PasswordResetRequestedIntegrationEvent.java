package github.muhsenerdev.users.api.application.auth;

import java.time.OffsetDateTime;
import java.util.UUID;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PasswordResetRequestedIntegrationEvent {
    private final UUID userId;
    private final String email;
    private final String code;
    private final OffsetDateTime expiresAt;
    private final String name;
}
