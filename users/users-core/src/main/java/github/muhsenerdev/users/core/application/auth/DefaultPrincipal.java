package github.muhsenerdev.users.core.application.auth;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import github.muhsenerdev.commons.core.auth.Principal;
import github.muhsenerdev.commons.core.vo.Email;
import github.muhsenerdev.commons.core.vo.Name;
import github.muhsenerdev.commons.core.vo.Username;
import github.muhsenerdev.users.core.domain.roles.Role;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DefaultPrincipal implements Principal {

    private final UUID userId;
    private final Email email;
    private final Username username;
    private final Name name;
    private final Set<Role> roles;

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
                .map(r -> r.getName().getValue())
                .collect(Collectors.toSet()) : Set.of();
    }
}
