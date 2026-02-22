package github.muhsenerdev.plans.domain.entitlement;

import java.time.OffsetDateTime;
import java.util.UUID;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import github.muhsenerdev.commons.jpa.entity.SoftDeletableEntity;
import github.muhsenerdev.plans.domain.feature.FeatureType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "entitlements")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLDelete(sql = "UPDATE entitlements SET deleted_at = CURRENT_TIMESTAMP WHERE id = ? AND version = ?")
@SQLRestriction("deleted_at IS NULL")
public class Entitlement extends SoftDeletableEntity {

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "subscription_id")
    private UUID subscriptionId;

    @Column(name = "feature_code", nullable = false)
    private String featureCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "feature_type", nullable = false)
    private FeatureType featureType;

    @Column(name = "total_amount")
    private Integer totalAmount;

    @Column(name = "used_amount")
    private Integer usedAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "source_type", nullable = false)
    private EntitlementSourceType sourceType;

    @Column(name = "valid_from")
    private OffsetDateTime validFrom;

    @Column(name = "valid_to")
    private OffsetDateTime validTo; // Null if infinite

    @Enumerated(EnumType.STRING)
    private EntitlementStatus status;

    public void cancel() {
        this.status = EntitlementStatus.CANCELLED;
    }

    public void expire() {
        this.status = EntitlementStatus.EXPIRED;
    }

    public void refresh(Integer totalAmount, OffsetDateTime validFrom, OffsetDateTime validTo) {
        this.totalAmount = totalAmount;
        this.usedAmount = 0;
        this.validFrom = validFrom;
        this.validTo = validTo;
        this.status = EntitlementStatus.ACTIVE;
    }

    public void useAmount(int amount) {
        if (this.usedAmount == null) {
            this.usedAmount = 0;
        }
        this.usedAmount += amount;
    }
}
