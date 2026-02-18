package github.muhsenerdev.plans.domain.plan;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface PlanReadRepository extends JpaRepository<Plan, UUID> {

    List<PlanListProjection> findAllProjectedBy();

    @Query("SELECT p FROM Plan p LEFT JOIN FETCH p.features pf LEFT JOIN FETCH pf.feature WHERE p.status = 'ACTIVE' AND pf.status = 'ACTIVE'")
    List<Plan> findActivePlansWithActiveFeatures();

    @Query("SELECT p FROM Plan p LEFT JOIN FETCH p.prices price WHERE p.status = 'ACTIVE' AND price.status = 'ACTIVE'")
    List<Plan> findActivePlansWithActicePrices();

}
