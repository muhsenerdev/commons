package github.muhsenerdev.plans.domain.subscription;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SubscriptionReadRepository
        extends Repository<Subscription, UUID>, JpaSpecificationExecutor<Subscription> {

    @Query("SELECT s.id as subscriptionId, s.interval as subscriptionInterval, s.currentPeriodStart as currentPeriodStart, s.currentPeriodEnd as currentPeriodEnd, s.planId as planId, p.name as planName, p.code as planCode, p.type as planType FROM Subscription s JOIN Plan p ON s.planId = p.id WHERE s.userId = :userId AND s.status = :status")
    Optional<UserSubscriptionPlanProjection> findByUserIdAndStatus(@Param("userId") UUID userId,
            @Param("status") SubscriptionStatus status);

    @Query("SELECT s.status FROM Subscription s WHERE s.id = :id AND s.userId = :userId")
    Optional<SubscriptionStatus> findStatusByIdAndUserId(@Param("id") UUID id, @Param("userId") UUID userId);
}
