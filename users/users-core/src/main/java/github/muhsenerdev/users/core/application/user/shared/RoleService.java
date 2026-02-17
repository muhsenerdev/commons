package github.muhsenerdev.users.core.application.user.shared;

import org.springframework.stereotype.Component;

import github.muhsenerdev.commons.core.vo.RoleName;
import github.muhsenerdev.users.core.domain.roles.Role;
import github.muhsenerdev.users.core.domain.roles.RoleRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;

    @SuppressWarnings("null")
    public Role getOrCreateUserRole() {
        return roleRepository.findByName(RoleName.user())
                .orElseGet(() -> roleRepository.save(Role.createUserRole()));
    }
}
