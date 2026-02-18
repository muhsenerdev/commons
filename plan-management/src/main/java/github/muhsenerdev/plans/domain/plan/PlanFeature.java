package github.muhsenerdev.plans.domain.plan;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import github.muhsenerdev.commons.core.exception.InvalidDomainException;
import github.muhsenerdev.commons.jpa.entity.SoftDeletableEntity;
import github.muhsenerdev.plans.domain.feature.Feature;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    protected static PlanFeature create(Plan plan, Feature feature, String value) {
        if (plan == null) {
            throw new InvalidDomainException("Plan cannot be null");
        }
        if (feature == null) {
            throw new InvalidDomainException("Feature cannot be null");
        }
        if (value == null || value.isEmpty()) {
            throw new InvalidDomainException("Feature value cannot be null or empty");
        }

        // TODO: validate feature value based on feature type

        return PlanFeature.builder()
                .plan(plan)
                .feature(feature)
                .value(value)
                .status(FeatureStatus.ACTIVE)
                .build();
    }

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
