package github.muhsenerdev.users.core.domain.users;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import github.muhsenerdev.commons.core.vo.Email;
import github.muhsenerdev.commons.core.vo.Username;

public interface UserRepository extends JpaRepository<User, UUID> {

    boolean existsByEmail(Email email);

    boolean existsByUsername(Username username);

    Optional<User> findByUsername(Username username);

    Optional<User> findByEmail(Email email);

    @Query("SELECT u FROM User u WHERE u.emailVerification.status = 'VERIFYING' AND u.emailVerification.expiresAt < :now")
    List<User> findExpiredRegistrations(@Param("now") OffsetDateTime now);

}
