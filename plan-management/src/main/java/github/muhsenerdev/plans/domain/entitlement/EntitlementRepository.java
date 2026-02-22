package github.muhsenerdev.plans.domain.entitlement;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import jakarta.persistence.LockModeType;

public interface EntitlementRepository extends JpaRepository<Entitlement, UUID>, JpaSpecificationExecutor<Entitlement> {

    @Query("SELECT e FROM Entitlement e WHERE e.subscriptionId = :subscriptionId AND e.validFrom <= :now AND e.validTo >= :now AND e.sourceType = :sourceType")
    List<Entitlement> findAllValidNowAndSubscriptionIdAndSourceType(@Param("subscriptionId") UUID subscriptionId,
            @Param("now") OffsetDateTime now, @Param("sourceType") EntitlementSourceType sourceType);

    @Query("SELECT e FROM Entitlement e WHERE e.userId = :userId AND e.featureCode = :featureCode AND e.validFrom <= :now AND (e.validTo IS NULL OR e.validTo >= :now) AND e.status = 'ACTIVE'")
    List<Entitlement> findAllActiveByUserIdAndFeatureCode(@Param("userId") UUID userId,
            @Param("featureCode") String featureCode, @Param("now") OffsetDateTime now);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT e FROM Entitlement e WHERE e.userId = :userId AND e.featureCode = :featureCode AND e.validFrom <= :now AND (e.validTo IS NULL OR e.validTo >= :now) AND e.status = 'ACTIVE' ORDER BY e.validTo ASC NULLS LAST")
    List<Entitlement> findBestEntitlementsForConsumption(@Param("userId") UUID userId,
            @Param("featureCode") String featureCode, @Param("now") OffsetDateTime now);

    List<Entitlement> findAllBySubscriptionId(UUID subscriptionId);

    List<Entitlement> findAllBySubscriptionIdAndStatus(UUID subscriptionId, EntitlementStatus status);

}
