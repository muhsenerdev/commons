package github.muhsenerdev.users.core.domain.users;

import java.util.UUID;
import java.util.function.Supplier;

import github.muhsenerdev.commons.core.event.DomainEvent;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder

public class UserCreated extends DomainEvent {

    private RegistrationType registrationType;
    private boolean verified;
    private Supplier<UUID> userIdSupplier;
}