package github.muhsenerdev.plans.domain.plan;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import github.muhsenerdev.commons.core.vo.Money;
import github.muhsenerdev.commons.jpa.entity.SoftDeletableEntity;
import github.muhsenerdev.plans.domain.shared.Interval;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
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
@Table(name = "plan_prices")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@SQLDelete(sql = "UPDATE plan_prices SET deleted_at = CURRENT_TIMESTAMP WHERE id = ? AND version = ?")
@SQLRestriction("deleted_at IS NULL")
public class PlanPrice extends SoftDeletableEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", nullable = false)
    private Plan plan;

    @Column(name = "stripe_price_id")
    private String stripePriceId;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "amount", nullable = false)),
            @AttributeOverride(name = "currency", column = @Column(name = "currency", nullable = false))
    })
    private Money price;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Interval priceInterval;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PriceStatus status;

    @Builder
    protected PlanPrice(Plan plan, Money price, Interval interval) {
        this.plan = plan;
        this.price = price;
        this.priceInterval = interval;
        this.status = PriceStatus.DRAFT;

    }

    protected void activate(String stripePriceId) {
        if (this.status != PriceStatus.DRAFT && this.status != PriceStatus.ARCHIVED) {
            throw PlanDomainException.priceCannotBeActivated("Plan price is not in draft or archived status");
        }
        this.stripePriceId = stripePriceId;
        this.status = PriceStatus.ACTIVE;
    }

    public boolean isDraft() {
        return this.status == PriceStatus.DRAFT;
    }

    public void archive() {
        this.status = PriceStatus.ARCHIVED;
    }

    public boolean isActive() {
        return this.status == PriceStatus.ACTIVE;
    }

    public void update(Money price, Interval interval) {
        if (this.status != PriceStatus.DRAFT) {
            throw PlanDomainException.priceCannotBeUpdated("Plan price is not in draft status");
        }
        this.price = price;
        this.priceInterval = interval;
    }

}
