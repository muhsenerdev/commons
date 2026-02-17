package github.muhsenerdev.users.core.application.user.shared;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import github.muhsenerdev.commons.core.vo.Email;
import github.muhsenerdev.users.api.application.auth.OidcUserDetails;
import github.muhsenerdev.users.api.application.registration.RegistrationHook;
import github.muhsenerdev.users.core.domain.roles.Role;
import github.muhsenerdev.users.core.domain.users.RegistrationType;
import github.muhsenerdev.users.core.domain.users.SocialLoginDetails;
import github.muhsenerdev.users.core.domain.users.User;
import github.muhsenerdev.users.core.domain.users.UserCreationInput;
import github.muhsenerdev.users.core.domain.users.UserRepository;
import github.muhsenerdev.users.core.infra.config.UserModuleProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class SocialLoginHandler {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final RoleService roleService;
    private final ApplicationEventPublisher eventPublisher;
    private final UserModuleProperties properties;
    private final List<RegistrationHook> registrationHooks;

    @Transactional
    public UserInfo handle(OidcUserDetails userDetails) {
        log.debug("Social login handling for email: {}", userDetails.email());
        SocialLoginDetails socialDetails = userMapper.extractSocialLoginDetails(userDetails);

        var user = userRepository.findWithRolesByEmail(Email.of(userDetails.email()))
                .map(existing -> {
                    existing.addNewSocialLogin(socialDetails);
                    return existing;
                }).orElseGet(() -> {
                    Set<String> missingUserDetails = detectMissingDetails(userDetails);
                    Set<Role> roles = Set.of(roleService.getOrCreateUserRole());
                    UserCreationInput input = UserCreationInput.builder()
                            .name(userDetails.name())
                            .username(userDetails.username())
                            .email(userDetails.email())
                            .roles(roles)
                            .registrationType(RegistrationType.SOCIAL)
                            .metadata(Map.of())
                            .missingDetails(missingUserDetails)
                            .socialLoginDetails(socialDetails)
                            .build();
                    return User.createSocialUser(input);
                });
        user.releaseEvents().forEach(eventPublisher::publishEvent);
        userRepository.save(user);

        return userMapper.toUserInfo(user);

    }

    private Set<String> detectMissingDetails(OidcUserDetails userDetails) {
        Set<String> missingDetails = new HashSet<>();
        if (properties.isUsernameRequired() && userDetails.username() == null) {
            missingDetails.add("username");
        }
        if (properties.isNameRequired() && userDetails.name() == null) {
            missingDetails.add("name");
        }
        registrationHooks.forEach(hook -> missingDetails.addAll(hook.getExtractDetails(userDetails)));
        return missingDetails;
    }

}
