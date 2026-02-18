package github.muhsenerdev.users.core.application.user.change_roles;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import github.muhsenerdev.commons.core.exception.NotFoundException;
import github.muhsenerdev.users.core.application.user.shared.RoleService;
import github.muhsenerdev.users.core.domain.users.UserRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PutUserRolesCommandHandler {

    private final UserRepository userRepository;
    private final RoleService roleService;

    @Transactional
    public void handle(PutUserRolesCommand command) {
        var user = userRepository.findByIdWithRoles(command.getUserId())
                .orElseThrow(() -> new NotFoundException("User not found with id: " + command.getUserId()));

        var roles = roleService.findRolesByNamesOrThrow(command.getRoles());

        user.changeRoles(roles);

        userRepository.save(user);
    }
}
