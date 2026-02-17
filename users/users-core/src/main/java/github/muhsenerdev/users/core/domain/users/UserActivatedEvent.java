package github.muhsenerdev.users.core.domain.users;

import java.util.UUID;
import java.util.function.Supplier;

import github.muhsenerdev.commons.core.event.DomainEvent;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class UserActivatedEvent extends DomainEvent {

    private final UUID userId;
    private final Supplier<UUID> userIdSupplier;

}
