package github.muhsenerdev.users.api.application.registration;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

import github.muhsenerdev.commons.core.event.BaseEvent;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class RegistrationCompletedEvent extends BaseEvent {

    private UUID userId;
    private String email;
    private String name;
    private String username;
    private String status;
    private String registrationType;
    private Set<String> roles;
    private Map<String, Object> metadata;

}
