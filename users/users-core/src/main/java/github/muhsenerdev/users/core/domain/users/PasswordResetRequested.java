package github.muhsenerdev.users.core.domain.users;

import java.time.OffsetDateTime;
import java.util.UUID;
import java.util.function.Supplier;

import github.muhsenerdev.commons.core.event.DomainEvent;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class PasswordResetRequested extends DomainEvent {
    private final Supplier<UUID> userIdSupplier;
    private final String email;
    private final String code;
    private final OffsetDateTime expiresAt;
    private final String name;

    public UUID getUserId() {
        return userIdSupplier.get();
    }
}
