package github.muhsenerdev.plans.domain.plan;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface PlanRepository extends JpaRepository<Plan, UUID> {
    boolean existsByCode(String code);

    @Query("SELECT p FROM Plan p LEFT JOIN FETCH p.prices WHERE p.id = :id")
    Optional<Plan> findWithPricesById(UUID id);

    @Query("SELECT p FROM Plan p LEFT JOIN FETCH p.features WHERE p.id = :id")
    Optional<Plan> findWithFeaturesById(UUID id);

    @Query("SELECT p FROM Plan p LEFT JOIN FETCH p.features pf LEFT JOIN FETCH pf.feature WHERE p.id = :id")
    Optional<Plan> findWithFeaturesDeeplyById(UUID id);

    boolean existsByTier(int tier);

}
