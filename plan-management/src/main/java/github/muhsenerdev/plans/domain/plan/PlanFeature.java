package github.muhsenerdev.plans.domain.plan;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import github.muhsenerdev.commons.jpa.entity.SoftDeletableEntity;
import github.muhsenerdev.plans.domain.feature.Feature;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Entity
@Table(name = "plan_features")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLDelete(sql = "UPDATE plan_features SET deleted_at = CURRENT_TIMESTAMP WHERE id = ? AND version = ?")
@SQLRestriction("deleted_at IS NULL")
public class PlanFeature extends SoftDeletableEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", nullable = false)
    private Plan plan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "feature_id", nullable = false)
    private Feature feature;

    @Column(name = "\"value\"", nullable = false)
    private String value; // e.g., "300", "true"

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private FeatureStatus status;

    public void archive() {
        this.status = FeatureStatus.INACTIVE;
    }

    public void activate() {
        this.status = FeatureStatus.ACTIVE;
    }

    public boolean isActive() {
        return this.status == FeatureStatus.ACTIVE;
    }
}
