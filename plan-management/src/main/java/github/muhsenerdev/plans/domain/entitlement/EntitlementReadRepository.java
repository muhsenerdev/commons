// package github.muhsenerdev.plans.domain.entitlement;

// import java.time.OffsetDateTime;
// import java.util.List;
// import java.util.UUID;

// import org.springframework.data.jpa.repository.JpaRepository;
// import org.springframework.data.jpa.repository.Query;

// import
// com.github.muhsenerdev.langpra.plans.application.entitlement.UserEntitlementProjection;

// public interface EntitlementReadRepository extends JpaRepository<Entitlement,
// UUID> {

// @Query("SELECT e FROM Entitlement e WHERE e.userId = :userId AND e.validFrom
// <= :now AND (e.validTo IS NULL OR e.validTo >= :now)")
// List<Entitlement> findValidEntitlementsByUserId(UUID userId, OffsetDateTime
// now);

// @Query("""
// SELECT
// e.featureCode AS featureCode,
// SUM(e.totalAmount) AS totalAmount,
// SUM(e.usedAmount) AS usedAmount
// FROM Entitlement e
// WHERE e.userId = :userId
// AND e.status = 'ACTIVE'
// AND e.validFrom <= :now
// AND (e.validTo IS NULL OR e.validTo >= :now)
// GROUP BY e.featureCode
// """)
// List<UserEntitlementProjection> findActiveEntitlementSummaryByUserId(UUID
// userId, OffsetDateTime now);

// }
