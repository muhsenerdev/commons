package github.muhsenerdev.users.core.domain.users;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import github.muhsenerdev.users.core.domain.roles.Role;
import github.muhsenerdev.users.core.domain.roles.RoleRepository;

public class UserDSL {

    private String username;
    private String name;

    public static UserDSL aUser() {
        return new UserDSL();
    }

    public User build() {

        var user = User.createPasswordUser(UserCreationInput.builder()
                .name(this.name)
                .username(this.username)
                .email(UUID.randomUUID().toString() + "@gmail.com")
                .password("hashed-password")
                .roles(Set.of(Role.createUserRole()))
                .verified(false)
                .metadata(Map.of())
                .build());

        return user;
    }

    public User save(RoleRepository roleRepository, UserRepository userRepository) {
        var user = build();
        Set<Role> newRoles = new HashSet<>();

        for (Role role : user.getRoles()) {
            Optional<Role> optionalRole = roleRepository.findByName(role.getName());
            if (optionalRole.isPresent()) {
                newRoles.add(optionalRole.get());
            } else {
                roleRepository.save(role);
                newRoles.add(role);
            }
        }
        user.setRoles(newRoles);
        userRepository.save(user);
        return user;
    }

    public UserDSL withUsername(String username) {
        this.username = username;
        return this;
    }

    public UserDSL withName(String name) {
        this.name = name;
        return this;
    }
}
