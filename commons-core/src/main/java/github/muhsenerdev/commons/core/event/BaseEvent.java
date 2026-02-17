package github.muhsenerdev.commons.core.event;

import java.time.OffsetDateTime;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@AllArgsConstructor
@Getter
@NoArgsConstructor
@SuperBuilder
public abstract class BaseEvent {
    @Builder.Default
    protected UUID eventId = UUID.randomUUID();
    @Builder.Default
    protected OffsetDateTime occurredAt = OffsetDateTime.now();
}
