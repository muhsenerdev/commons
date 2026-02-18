package github.muhsenerdev.users.core.domain.users;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface UserReadRepository extends JpaRepository<User, UUID>, JpaSpecificationExecutor<User> {
}
