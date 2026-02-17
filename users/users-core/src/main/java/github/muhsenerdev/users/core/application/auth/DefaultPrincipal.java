package github.muhsenerdev.users.core.application.auth;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import github.muhsenerdev.commons.core.auth.Principal;
import github.muhsenerdev.commons.core.vo.Email;
import github.muhsenerdev.commons.core.vo.Name;
import github.muhsenerdev.commons.core.vo.RoleName;
import github.muhsenerdev.commons.core.vo.Username;
import github.muhsenerdev.users.core.domain.users.UserStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DefaultPrincipal implements Principal {

    private final UUID userId;
    private final Email email;
    private final Username username;
    private final Name name;
    private final Set<RoleName> roles;
    private final Set<String> missingDetails;
    private final UserStatus status;

    @Override
    public UUID getUserId() {
        return userId;
    }

    @Override
    public String getEmail() {
        return email != null ? email.getValue() : null;
    }

    @Override
    public String getUsername() {
        return username != null ? username.getValue() : null;
    }

    @Override
    public String getName() {
        return name != null ? name.getValue() : null;
    }

    @Override
    public Set<String> getRoles() {
        return roles != null ? roles.stream()
                .map(RoleName::getValue)
                .collect(Collectors.toSet()) : Set.of();
    }

    @Override
    public Set<String> getMissingDetails() {
        return missingDetails != null ? missingDetails : Set.of();
    }

    @Override
    public boolean isActive() {
        return status == UserStatus.ACTIVE;
    }

    @Override
    public String getStatus() {
        return status.name();
    }
}
