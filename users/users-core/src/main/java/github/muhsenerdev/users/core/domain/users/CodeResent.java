package github.muhsenerdev.users.core.domain.users;

import java.time.OffsetDateTime;
import java.util.UUID;

import github.muhsenerdev.commons.core.event.DomainEvent;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
public class CodeResent extends DomainEvent {

    private UUID userId;
    private String email;
    private String newCode;
    private OffsetDateTime expiresAt;
    private String name;

}
