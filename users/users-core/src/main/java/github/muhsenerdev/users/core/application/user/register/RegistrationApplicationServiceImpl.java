package github.muhsenerdev.users.core.application.user.register;

import java.util.Set;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import github.muhsenerdev.commons.core.vo.RoleName;
import github.muhsenerdev.users.api.application.registration.RegisterUserBaseCommand;
import github.muhsenerdev.users.api.application.registration.RegistrationApplicationService;
import github.muhsenerdev.users.api.application.registration.UserRegistrationResponse;
import github.muhsenerdev.users.api.application.registration.UserRegistrationStrategy;
import github.muhsenerdev.users.core.application.user.shared.UserMapper;
import github.muhsenerdev.users.core.domain.roles.Role;
import github.muhsenerdev.users.core.domain.roles.RoleRepository;
import github.muhsenerdev.users.core.domain.users.RegistrationType;
import github.muhsenerdev.users.core.domain.users.UserCreationInput;
import github.muhsenerdev.users.core.domain.users.UserDomainService;
import github.muhsenerdev.users.core.domain.users.UserRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RegistrationApplicationServiceImpl<T extends RegisterUserBaseCommand>
        implements RegistrationApplicationService<T> {

    private final UserDomainService userDomainService;
    private final UserRepository userRepository;
    private final UserRegistrationStrategy<T> strategy;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final ApplicationEventPublisher eventPublisher;

    @SuppressWarnings("null")
    @Override
    public UserRegistrationResponse register(T command) {
        strategy.validate(command);

        // Create user
        UserCreationInput input = userMapper.toCreationInput(command,
                RegistrationType.PASSWORD,
                Set.of(getUserRole()),
                false);
        var user = userDomainService.createUser(input);

        // Save user
        userRepository.save(user);

        eventPublisher.publishEvent(userMapper.toUserRegisteredEvent(user));
        return UserRegistrationResponse.builder()
                .id(user.getId())
                .build();
    }

    @SuppressWarnings("null")
    private Role getUserRole() {
        return roleRepository.findByName(RoleName.user())
                .orElseGet(() -> roleRepository.save(Role.createUserRole()));
    }

}
