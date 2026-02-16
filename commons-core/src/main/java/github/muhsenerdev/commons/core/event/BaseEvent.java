package github.muhsenerdev.commons.core.event;

import java.time.OffsetDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Getter
@NoArgsConstructor
public abstract class BaseEvent {
    protected UUID eventId = UUID.randomUUID();
    protected OffsetDateTime occurredAt = OffsetDateTime.now();
}
