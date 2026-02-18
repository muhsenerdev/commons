package github.muhsenerdev.users.core.application.user.shared;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import github.muhsenerdev.commons.core.exception.NotFoundException;
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

    public Set<Role> getRolesByIdsOrThrow(Set<UUID> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            return Collections.emptySet();
        }

        Set<UUID> rolesNotFound = new HashSet<>(roleIds);

        var rolesFound = roleRepository.findAllById(roleIds).stream()
                .map(role -> {
                    rolesNotFound.remove(role.getId());
                    return role;
                }).collect(Collectors.toSet());

        if (!rolesNotFound.isEmpty()) {
            throw new NotFoundException("Roles not found: " + rolesNotFound);
        }

        return rolesFound;
    }

    public Set<Role> findRolesByNamesOrThrow(Set<RoleName> names) {
        if (names == null || names.isEmpty()) {
            return Collections.emptySet();
        }

        Set<RoleName> rolesNotFound = new HashSet<>(names);

        var rolesFound = roleRepository.findAllByNameIn(names).stream()
                .map(role -> {
                    rolesNotFound.remove(role.getName());
                    return role;
                }).collect(Collectors.toSet());

        if (!rolesNotFound.isEmpty()) {
            throw new NotFoundException("Roles not found: " + rolesNotFound);
        }

        return rolesFound;
    }
}
