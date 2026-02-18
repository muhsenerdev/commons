package github.muhsenerdev.users.core.application.user.register;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import github.muhsenerdev.users.api.application.registration.RegistrationHook;
import github.muhsenerdev.users.core.application.user.shared.RoleService;
import github.muhsenerdev.users.core.application.user.shared.UserMapper;
import github.muhsenerdev.users.core.domain.roles.Role;
import github.muhsenerdev.users.core.domain.users.RegistrationType;
import github.muhsenerdev.users.core.domain.users.UserCreationInput;
import github.muhsenerdev.users.core.domain.users.UserDomainService;
import github.muhsenerdev.users.core.domain.users.UserRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RegisterUserCommandHandler {

    private final List<RegistrationHook> registrationHooks;
    private final UserMapper userMapper;
    private final RoleService roleService;
    private final UserDomainService userDomainService;
    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public UserRegistrationResponse handle(RegisterUserCommand command) {
        registrationHooks.forEach(hook -> hook.validateRegistrationDetails(command.getOtherDetails()));
        // Create user
        UserCreationInput input = userMapper.toCreationInput(command,
                RegistrationType.PASSWORD,
                fetchRoles(command.getRoleIds()),
                command.isVerified());
        var user = userDomainService.createUser(input);

        user.releaseEvents().forEach(eventPublisher::publishEvent);

        // Save user
        userRepository.save(user);
        user.releaseEvents().forEach(eventPublisher::publishEvent);

        return UserRegistrationResponse.builder()
                .id(user.getId())
                .build();
    }

    private Set<Role> fetchRoles(Set<UUID> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            return Set.of(roleService.getOrCreateUserRole());
        }
        return roleService.getRolesByIdsOrThrow(roleIds);
    }
}
