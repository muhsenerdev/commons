package github.muhsenerdev.plans.domain.subscription;

import java.time.Duration;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Map;
import java.util.UUID;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;
import org.hibernate.type.SqlTypes;

import github.muhsenerdev.commons.core.exception.InvalidDomainException;
import github.muhsenerdev.commons.jpa.entity.SoftDeletableEntity;
import github.muhsenerdev.plans.domain.feature.FeatureType;
import github.muhsenerdev.plans.domain.plan.Plan;
import github.muhsenerdev.plans.domain.plan.PlanPrice;
import github.muhsenerdev.plans.domain.shared.Interval;
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
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "subscriptions")
@Getter
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE subscriptions SET deleted_at = CURRENT_TIMESTAMP WHERE id = ? AND version = ?")
@SQLRestriction("deleted_at IS NULL")
public class Subscription extends SoftDeletableEntity {

    public static final Duration GRACE_PERID_DURATION = Duration.ofDays(3);

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "price_id")
    private UUID priceId;

    @Column(name = "plan_id", nullable = false)
    private UUID planId;

    @Column(name = "provider_id")
    private String providerId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SubscriptionStatus status;

    @Column(name = "subscription_interval")
    @Enumerated(EnumType.STRING)
    private Interval interval;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "entitlements_snapshot")
    private Map<String, EntitlementsSnapshot> entitlements;

    @Column(name = "current_period_start")
    private OffsetDateTime currentPeriodStart;

    @Column(name = "current_period_end")
    private OffsetDateTime currentPeriodEnd;

    @Column(name = "next_maintenance_date")
    private OffsetDateTime nextMaintenanceDate;

    @Column(name = "grace_until")
    private OffsetDateTime graceUntil;

    @Column(name = "cancellation_reason")
    private String cancellationReason;

    @Enumerated(EnumType.STRING)
    @Column(name = "maintenance_mode")
    private MaintenanceMode maintenanceMode;

    @Column(name = "auto_maintenance", nullable = false)
    private boolean autoMaintenance;

    @Column(name = "maintenance_count", nullable = false)

    private int maintenanceCount = 0;

    @Column(name = "last_maintenance_date")
    private OffsetDateTime lastMaintenanceDate;

    @Column(name = "entitlement_valid_from")
    private OffsetDateTime entitlementValidFrom;

    @Column(name = "is_default", nullable = false)
    private boolean isDefault = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "price_id", nullable = false, insertable = false, updatable = false)
    private PlanPrice price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", nullable = false, insertable = false, updatable = false)
    private Plan plan;

    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    @Getter
    @EqualsAndHashCode
    public static class EntitlementsSnapshot {
        private FeatureType type;
        private String value;
    }

    // @Builder
    // public Subscription(UUID userId, UUID priceId, UUID planId, Interval
    // interval,
    // Map<String, EntitlementsSnapshot> entitlements, MaintenanceMode
    // maintenanceMode,
    // boolean autoMaintenance) {
    // this.userId = userId;
    // this.priceId = priceId;
    // this.planId = planId;
    // this.interval = interval;
    // this.entitlements = entitlements;
    // this.maintenanceMode = maintenanceMode;
    // this.autoMaintenance = autoMaintenance;
    // this.status = SubscriptionStatus.PENDING;
    // }

    @Builder
    public Subscription(UUID userId, UUID priceId, UUID planId, Interval interval,
            Map<String, EntitlementsSnapshot> entitlements) {
        this.userId = userId;
        this.priceId = priceId;
        this.planId = planId;
        this.interval = interval;
        this.entitlements = entitlements;
        this.status = SubscriptionStatus.PENDING;

        // This is fragile and should be removed in the future
        if (priceId == null && interval == Interval.INFINITE) {
            this.isDefault = true;
        }

    }

    /**
     * Activates the subscription if it is in PENDING state.
     * If the subscription is already active or in any other state, it does nothing.
     * 
     * @param details
     * @return true if the subscription was activated, false otherwise
     */
    public boolean activate(SubscriptionInfo details) {
        if (!isPending()) {
            return false;
        }

        this.status = SubscriptionStatus.ACTIVE;
        this.providerId = details.providerId();

        ZoneOffset utc = ZoneOffset.UTC;
        this.currentPeriodStart = details.periodStart().atOffset(utc);
        this.entitlementValidFrom = currentPeriodStart;
        this.nextMaintenanceDate = currentPeriodStart.plusMonths(1);

        if (isInfinite()) {
            this.maintenanceMode = MaintenanceMode.FROM_PLAN;
            this.autoMaintenance = true;
            this.currentPeriodEnd = null;
            this.graceUntil = null;
            this.maintenanceCount = 0;
        } else if (isMonthly()) {
            this.maintenanceMode = MaintenanceMode.FROM_PLAN;
            this.autoMaintenance = false;
            this.currentPeriodEnd = details.periodEnd().atOffset(utc);
            this.graceUntil = currentPeriodEnd.plus(GRACE_PERID_DURATION);
            this.maintenanceCount = 0;
        } else if (isYearly()) {
            this.maintenanceMode = MaintenanceMode.FROM_SNAPSHOT;
            this.autoMaintenance = true;
            this.currentPeriodEnd = details.periodEnd().atOffset(utc);
            this.graceUntil = currentPeriodEnd.plus(GRACE_PERID_DURATION);
            this.maintenanceCount = 0;
        }
        return true;
    }

    private boolean isInfinite() {
        return this.interval == Interval.INFINITE;
    }

    public boolean isMonthly() {
        return this.interval == Interval.MONTHLY;
    }

    public boolean isYearly() {
        return this.interval == Interval.YEARLY;
    }

    public boolean isPending() {
        return this.status == SubscriptionStatus.PENDING;
    }

    public void expireCheckout() {
        if (isDefault) {
            return;
        }
        if (!isPending()) {
            return;
        }
        this.status = SubscriptionStatus.CHECKOUT_EXPIRED;
    }

    public void renew(Instant periodStart, Instant periodEnd, Map<String, EntitlementsSnapshot> entitlements) {
        if (isDefault) {
            return;
        }
        if (is(SubscriptionStatus.ACTIVE) || is(SubscriptionStatus.PAST_DUE)) {
            this.currentPeriodStart = periodStart.atOffset(ZoneOffset.UTC);
            this.currentPeriodEnd = periodEnd.atOffset(ZoneOffset.UTC);
            this.nextMaintenanceDate = currentPeriodStart.plusMonths(1);
            this.graceUntil = currentPeriodEnd.plus(GRACE_PERID_DURATION);
            this.entitlements = entitlements;
            this.entitlementValidFrom = currentPeriodStart;
        }

    }

    public void grace() {
        if (isDefault) {
            return;
        }
        if (isActive()) {
            this.status = SubscriptionStatus.PAST_DUE;
        }
    }

    public void cancel(String reason) {
        if (isDefault) {
            return;
        }
        this.status = SubscriptionStatus.CANCELLED;
        this.cancellationReason = reason;
    }

    public void scheduleCancellation(String reason) {
        if (isDefault) {
            return;
        }
        this.status = SubscriptionStatus.CANCEL_AT_PERIOD_END;
        this.cancellationReason = reason;
    }

    public boolean isActive() {
        return this.status == SubscriptionStatus.ACTIVE;
    }

    public boolean isPastDue() {
        return this.status == SubscriptionStatus.PAST_DUE;
    }

    public boolean is(SubscriptionStatus status) {
        return this.status == status;
    }

    public void doAutoMaintenance(Map<String, EntitlementsSnapshot> entitlementSnaphots) {
        if (!autoMaintenance) {
            throw new InvalidDomainException("Subscription is not in auto maintenance mode");
        }
        if (this.status != SubscriptionStatus.ACTIVE && this.status != SubscriptionStatus.CANCEL_AT_PERIOD_END) {
            throw new InvalidDomainException("Only active subscriptions can be reset automatically.");
        }

        if (nextMaintenanceDate.isAfter(OffsetDateTime.now(ZoneOffset.UTC))) {
            throw new InvalidDomainException("subscription.maintenance.not.ready",
                    "Next maintenance date is in the future. Please try again later that: {}",
                    nextMaintenanceDate);
        }
        if (maintenanceMode == MaintenanceMode.FROM_PLAN) {
            if (entitlementSnaphots == null) {
                throw new InvalidDomainException("To do maintenance from plan, you send up-to-date entitlements");
            }
            this.entitlements = entitlementSnaphots;
        }

        OffsetDateTime old = this.nextMaintenanceDate;
        this.nextMaintenanceDate = this.nextMaintenanceDate.plusMonths(1);
        this.entitlementValidFrom = old;
        this.lastMaintenanceDate = OffsetDateTime.now(ZoneOffset.UTC);
        this.maintenanceCount++;
        if (isYearly() && maintenanceCount == 11) {
            this.autoMaintenance = false;
        }

    }
}
