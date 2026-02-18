package github.muhsenerdev.users.core.domain.roles;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import github.muhsenerdev.commons.core.vo.RoleName;

@Repository
public interface RoleRepository extends JpaRepository<Role, UUID> {

    Optional<Role> findByName(RoleName name);

    Set<Role> findAllByNameIn(Set<RoleName> names);

    boolean existsByName(RoleName name);

}
