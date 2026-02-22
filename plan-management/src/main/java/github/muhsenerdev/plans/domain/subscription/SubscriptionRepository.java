package github.muhsenerdev.plans.domain.subscription;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SubscriptionRepository extends JpaRepository<Subscription, UUID> {
    List<Subscription> findAllByStatusAndCreatedAtBefore(SubscriptionStatus status, Instant createdAt);

    Optional<Subscription> findByIdAndUserId(UUID id, UUID userId);

    List<Subscription> findAllByAutoMaintenanceTrueAndNextMaintenanceDateBefore(OffsetDateTime nextMaintenanceDate);

    @Query("""
                SELECT  count(*) > 0
                FROM Subscription s
                WHERE s.userId = :userId
                AND s.status IN ('PENDING', 'ACTIVE', 'PAST_DUE')
                AND s.isDefault = false
            """)
    boolean existsActiveSubscriptionOtherThanDefault(UUID userId);
}
