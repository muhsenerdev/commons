package github.muhsenerdev.users.api.application.registration;

import java.time.OffsetDateTime;
import java.util.UUID;

import github.muhsenerdev.commons.core.event.BaseEvent;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserRegisteredEvent extends BaseEvent {

    private UUID userId;
    private String email;
    private String name;
    private String username;
    private OffsetDateTime verificationExpiresAt;
    private String verificationCode;

}
