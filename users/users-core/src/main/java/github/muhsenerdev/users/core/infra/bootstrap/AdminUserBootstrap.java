package github.muhsenerdev.users.core.infra.bootstrap;

import java.util.Set;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import github.muhsenerdev.commons.core.vo.Email;
import github.muhsenerdev.commons.core.vo.RoleName;
import github.muhsenerdev.users.core.domain.roles.Role;
import github.muhsenerdev.users.core.domain.roles.RoleRepository;
import github.muhsenerdev.users.core.domain.users.RegistrationType;
import github.muhsenerdev.users.core.domain.users.UserCreationInput;
import github.muhsenerdev.users.core.domain.users.UserDomainService;
import github.muhsenerdev.users.core.domain.users.UserRepository;
import github.muhsenerdev.users.core.infra.config.UserModuleProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class AdminUserBootstrap implements CommandLineRunner {

    private final UserDomainService userDomainService;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserModuleProperties properties;

    @Override
    @Transactional
    public void run(String... args) {
        var adminProps = properties.getAdmin();

        if (adminProps == null || adminProps.getEmail() == null || adminProps.getPassword() == null) {
            throw new IllegalStateException(
                    "Admin user bootstrap failed: credentials not provided in app.users.admin configuration.");
        }

        Email adminEmail = Email.of(adminProps.getEmail());
        if (userRepository.existsByEmail(adminEmail)) {
            log.info("Admin user bootstrap skipped: user with email {} already exists.", adminProps.getEmail());
            return;
        }

        log.info("Starting admin user bootstrap for email: {}", adminProps.getEmail());

        Role adminRole = roleRepository.findByName(RoleName.admin())
                .orElseGet(() -> {
                    log.info("Admin role not found, creating it.");
                    return roleRepository.save(Role.createAdminRole());
                });

        UserCreationInput input = UserCreationInput.builder()
                .email(adminProps.getEmail())
                .password(adminProps.getPassword())
                .name("System Admin")
                .username("admin")
                .roles(Set.of(adminRole))
                .registrationType(RegistrationType.PASSWORD)
                .verified(true)
                .build();

        var adminUser = userDomainService.createUser(input);
        userRepository.save(adminUser);

        log.info("Admin user bootstrapped successfully.");
    }
}
