package github.muhsenerdev.users.core.domain.users;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import github.muhsenerdev.commons.core.vo.Email;
import github.muhsenerdev.commons.core.vo.Name;
import github.muhsenerdev.commons.core.vo.Username;
import github.muhsenerdev.users.core.domain.roles.Role;
import github.muhsenerdev.users.core.domain.roles.RoleRepository;

public class UserDSL {

    private String username;
    private String name;

    public static UserDSL aUser() {
        return new UserDSL();
    }

    public User build() {
        Username username = null;
        if (this.username != null) {
            username = Username.of(this.username);
        }

        Name name = null;
        if (this.name != null) {
            name = Name.of(this.name);
        }

        var user = User.createPasswordUser(name, username, Email.of(UUID.randomUUID().toString() + "@gmail.com"),
                HashedPassword.of("hashed-password"),
                Set.of(Role.createUserRole()), false, Map.of());

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
