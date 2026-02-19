package github.muhsenerdev.plans.domain.plan;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

import com.fasterxml.jackson.annotation.JsonIgnore;

import github.muhsenerdev.commons.core.exception.InvalidDomainException;
import github.muhsenerdev.commons.core.exception.InvalidInputException;
import github.muhsenerdev.commons.core.vo.Money;
import github.muhsenerdev.commons.jpa.entity.SoftDeletableEntity;
import github.muhsenerdev.plans.domain.feature.Feature;
import github.muhsenerdev.plans.domain.shared.Interval;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "plans")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE plans SET deleted_at = CURRENT_TIMESTAMP WHERE id = ? AND version = ?")
@SQLRestriction("deleted_at IS NULL")
public class Plan extends SoftDeletableEntity {

    @Column(nullable = false)
    private String code;

    @Column(name = "description", columnDefinition = "text")
    private String description;

    private String title;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PlanType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PlanStatus status;

    @OneToMany(mappedBy = "plan", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PlanPrice> prices;

    @OneToMany(mappedBy = "plan", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PlanFeature> features;

    @Column(name = "stripe_product_id", unique = true, nullable = true)
    private String stripeProductId;

    @Column(nullable = false, name = "tier")
    private int tier;

    @Column(name = "activation_fail_reason", columnDefinition = "text")
    private String activationFailReason;

    @Transient
    private PriceFacade priceFacade = new PriceFacade(this);

    @Transient
    private FeatureFacade featureFacade = new FeatureFacade(this);

    @Builder
    public Plan(String code, String description, String title, String name, PlanType type, int tier) {
        this.code = code;
        this.description = description;
        this.title = title;
        this.name = name;
        this.type = type;
        this.tier = tier;
        this.status = PlanStatus.DRAFT;
        this.prices = new ArrayList<>();
        this.features = new ArrayList<>();
        validate();
    }

    public static Plan createDraft(PlanInput input) {
        return Plan.builder()
                .code(input.code())
                .description(input.description())
                .title(input.title())
                .name(input.name())
                .type(input.type())
                .tier(input.tier())
                .build();
    }

    private void validate() {
        if (code == null || code.isBlank() || code.length() < 3 || code.length() > 50) {
            throw new InvalidDomainException("Plan code must be between 3 and 50 characters");
        }

        if (description != null && (description.length() < 3 || description.length() > 500)) {
            throw new InvalidDomainException("Plan description must be between 3 and 500 characters");
        }

        if (title == null || title.isBlank() || title.length() < 3 || title.length() > 255) {
            throw new InvalidDomainException("Plan title must be between 3 and 255 characters");
        }

        if (name == null || name.isBlank() || name.length() < 3 || name.length() > 255) {
            throw new InvalidDomainException("Plan name must be between 3 and 255 characters");
        }

        if (type == null) {
            throw new InvalidDomainException("Plan type cannot be null");
        }

        if (status == null) {
            throw new InvalidDomainException("Plan status cannot be null");
        }

        if (tier <= 0) {
            throw new InvalidDomainException("Plan tier cannot be negative or zero");
        }

    }

    public void activationFailed(String reason) {
        if (status == PlanStatus.ACTIVATING) {
            this.status = PlanStatus.ACTIVATION_FAILED;
            this.activationFailReason = reason;
            this.prices.forEach(p -> p.activationFailed(reason));
        }

    }

    public void reserveForActivation() throws PlanDomainException {
        tryActivate();
        this.status = PlanStatus.ACTIVATING;
        this.prices.forEach(price -> price.reserveForActivation());
    }

    private void tryActivate() {
        if (!isDraft()) {
            throw PlanDomainException.illegalOperation("To activate a plan, it must be in DRAFT status.");
        }
        // Check if it has any feature.
        if (this.features.isEmpty()) {
            throw PlanDomainException.atleastOneFeature("Plan must have at least one feature to activate.");
        }

        // Check if it has any price if it is PAID plan.
        if (!isFree() && this.prices.isEmpty()) {
            throw PlanDomainException.atleastOnePrice("PAID plans must have at least one price to activate.");
        }
    }

    public void activate(String providerId, Map<UUID, String> priceProviderIds) {
        if (this.status != PlanStatus.ACTIVATING) {
            throw PlanDomainException.illegalOperation("Plan must be in ACTIVATING status to be activated");
        }

        this.stripeProductId = providerId;
        this.status = PlanStatus.ACTIVE;
        this.activationFailReason = null;

        if (!isFree() && priceProviderIds != null) {
            this.prices.forEach(price -> {
                String priceProviderId = priceProviderIds.get(price.getId());
                if (priceProviderId != null) {
                    price.activate(priceProviderId);
                }
            });
        }
    }

    @JsonIgnore
    public boolean isFree() {
        return this.type == PlanType.FREE;
    }

    // public void updateBasics(String name, String title, String description, int
    // tier) {
    // this.name = name;
    // this.title = title;
    // this.description = description;
    // this.tier = tier;
    // validate();
    // }

    // ====================================
    // =========== UPDATE PLAN ============
    // ====================================

    public void updateFull(PlanInput input) {
        if (this.status != PlanStatus.DRAFT) {
            throw PlanDomainException.illegalOperation("Only DRAFT plans can be updated. Plan Status: {}",
                    this.status.name());
        }
        this.name = input.name();
        this.title = input.title();
        this.description = input.description();
        this.type = input.type();
        this.code = input.code();
        this.tier = input.tier();
        validate();
    }

    public void addFeature(Feature feature, String value) {
        if (feature == null) {
            throw new InvalidInputException("Feature cannot be null");
        }

        if (value == null || value.isEmpty()) {
            throw new InvalidInputException("Feature value cannot be null or empty");
        }

        boolean featureExists = this.features.stream()
                .anyMatch(f -> f.getFeature().getId().equals(feature.getId()));

        if (featureExists) {
            throw PlanDomainException.duplicateFeature(feature.getCode());
        }

        PlanFeature planFeature = PlanFeature.builder()
                .plan(this)
                .feature(feature)
                .value(value)
                .status(FeatureStatus.ACTIVE)
                .build();
        this.features.add(planFeature);
    }

    public Optional<PlanFeature> getLastFeature() {
        return this.features.isEmpty() ? Optional.empty() : Optional.of(this.features.getLast());
    }

    public boolean isActive() {
        return this.status == PlanStatus.ACTIVE;
    }

    public boolean existsPriceById(UUID targetPriceId) {
        return this.prices.stream()
                .anyMatch(price -> price.getId().equals(targetPriceId));
    }

    // ========== ACTIVATE PLAN PRICE ==========

    public boolean arePlanAndPriceActive(UUID priceId) {
        return this.status == PlanStatus.ACTIVE && this.prices.stream()
                .filter(price -> price.getId().equals(priceId))
                .findFirst()
                .map(PlanPrice::isActive)
                .orElse(false);
    }

    /**
     * Checks if the plan can be deleted. If not, throws a PlanDomainException.
     * 
     * @throws PlanDomainException if the plan is not in DRAFT status
     */
    public void delete() throws PlanDomainException {
        if (this.status != PlanStatus.DRAFT) {
            throw PlanDomainException.illegalOperation("Only plans in DRAFT status can be deleted.");
        }
    }

    public void archive() {
        if (this.status != PlanStatus.ACTIVE) {
            throw PlanDomainException.cannotBeArchived("Plan must be in ACTIVE status to be archived");
        }
        this.status = PlanStatus.ARCHIVED;
        this.prices.forEach(PlanPrice::archive);
        this.features.forEach(PlanFeature::archive);
    }

    public boolean isArchived() {
        return this.status == PlanStatus.ARCHIVED;
    }

    public boolean isDraft() {
        return this.status == PlanStatus.DRAFT;
    }

    public PriceFacade prices() {
        return priceFacade;
    }

    public FeatureFacade features() {
        return featureFacade;
    }

    private void ensurePriceUniquenessOn(Interval interval, PriceStatus status) {
        boolean hasPriceOnTheSameInterval = this.hasPrice(interval, status);
        if (hasPriceOnTheSameInterval) {
            throw new PlanDomainException("plan.duplicate_price",
                    "Plan has already prices on the same interval: {} in status: {}", interval, status);
        }
    }

    private Optional<PlanPrice> findFirstPrice(Interval interval, PriceStatus status) {
        return this.prices.stream()
                .filter(price -> price.getPriceInterval().equals(interval))
                .filter(price -> price.getStatus().equals(status))
                .findFirst();
    }

    private boolean hasPrice(Interval interval, PriceStatus draft) {
        return this.findFirstPrice(interval, draft)
                .isPresent();
    }

    private boolean hasFeature(Feature feature) {
        return this.features.stream()
                .filter(f -> f.getFeature().getId().equals(feature.getId()))
                .findFirst()
                .isPresent();
    }

    private PlanFeature getFeatureOrThrow(UUID featureId) {
        return getPlanFeature(featureId)
                .orElseThrow(() -> PlanDomainException.featureNotFound(featureId));
    }

    private Optional<PlanFeature> getPlanFeature(UUID id) {
        return this.features.stream()
                .filter(f -> f.getId().equals(id))
                .findFirst();
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class PriceFacade {

        private Plan plan;

        public PlanPrice add(Money price, Interval interval) {
            if (plan.isFree()) {
                throw PlanDomainException.illegalOperation("Price cannot be added to FREE plans.");
            }

            if (!plan.isDraft() && !plan.isActive()) {
                throw PlanDomainException.illegalOperation("Plan must be in DRAFT or ACTIVE status to add price");
            }
            plan.ensurePriceUniquenessOn(interval, PriceStatus.DRAFT);
            PlanPrice planPrice = PlanPrice.builder()
                    .plan(plan)
                    .price(price)
                    .interval(interval)
                    .build();
            plan.prices.add(planPrice);
            return planPrice;
        }

        public PlanPrice getLast() {
            return plan.prices.getLast();
        }

        /**
         * Removes price from plan, if it is DRAFT.
         * 
         * @param priceId priceId to be deleted
         * @throws PlanDomainException If price not found, or price is not in DRAFT
         *                             status
         */
        public void delete(UUID priceId) throws PlanDomainException {
            if (priceId == null) {
                throw new InvalidInputException("priceId cannot be null.");
            }

            if (!plan.isDraft() && !plan.isActive()) {
                throw PlanDomainException.illegalOperation("Plan must be in DRAFT or ACTIVE status to delete price");
            }
            Optional<PlanPrice> priceOpt = plan.prices.stream()
                    .filter(p -> p.getId().equals(priceId))
                    .findFirst();

            if (priceOpt.isEmpty()) {
                return;
            }

            PlanPrice price = priceOpt.get();
            if (!price.isDraft()) {
                throw PlanDomainException.illegalOperation("Only prices in DRAFT status can be deleted");
            }

            plan.prices.remove(price);
        }

        // public void update(UUID priceId, Money price, Interval newInterval) {
        // PlanPrice planPrice = plan.prices.stream()
        // .filter(p -> p.getId().equals(priceId))
        // .findFirst()
        // .orElseThrow(() -> PlanDomainException.priceNotFound(priceId));

        // Interval priceOldInterval = planPrice.getPriceInterval();
        // if (!Objects.equals(priceOldInterval, newInterval)) {
        // plan.ensurePriceUniquenessOn(newInterval, planPrice.getStatus());
        // }
        // planPrice.update(price, newInterval);

        // }

        /**
         * Marks the price as ARCHIVING. If price not found, then ignores.
         * 
         * @param priceId priceId to be archived
         * @throws PlanDomainException If plan or price is not active status.
         */
        public void reserveForArchive(UUID priceId) throws PlanDomainException {
            if (!plan.isActive()) {
                throw PlanDomainException.illegalOperation(
                        "To archive a price, plan must be in ACTIVE status, but found: {}", plan.status.name());
            }
            if (!hasAnyPricesOtherThan(priceId)) {
                throw PlanDomainException.atleastOnePrice(
                        "Active plan must have at least one active price.");
            }

            findPrice(priceId).ifPresent(p -> p.reserveForArchiving());
        }

        public void reserveForActivation(UUID priceId, boolean overrideActivePrice) {
            if (!plan.isActive()) {
                throw PlanDomainException.illegalOperation(
                        "To activate a price, plan must be in ACTIVE status, but found: {}", plan.status.name());
            }

            PlanPrice targetPrice = findPriceOrThrow(priceId);

            Optional<PlanPrice> activePriceForSameInterval = plan.findFirstPrice(targetPrice.getPriceInterval(),
                    PriceStatus.ACTIVE)
                    .filter(price -> !price.getId().equals(priceId));

            if (activePriceForSameInterval.isPresent()) {
                if (overrideActivePrice) {
                    activePriceForSameInterval.get().reserveForArchiving();
                } else {
                    throw PlanDomainException.illegalOperation(
                            "An active price already exists for interval: " + targetPrice.getPriceInterval());
                }
            }

            targetPrice.reserveForActivation();
        }

        private Optional<PlanPrice> findPrice(UUID priceId) {
            return plan.prices.stream()
                    .filter(price -> price.getId().equals(priceId))
                    .findFirst();
        }

        private PlanPrice findPriceOrThrow(UUID priceId) {
            return findPrice(priceId)
                    .orElseThrow(() -> PlanDomainException.priceNotFound(priceId));
        }

        private boolean hasAnyPricesOtherThan(UUID priceId) {
            return plan.prices.stream()
                    .filter(price -> !price.getId().equals(priceId))
                    .findFirst()
                    .isPresent();
        }

        public void finalizeActivation(UUID priceId, String providerPriceId) {
            PlanPrice targetPrice = findPriceOrThrow(priceId);
            targetPrice.activate(providerPriceId);
        }

        public void markAsFailed(UUID priceId, String reason) {
            plan.prices.stream()
                    .filter(price -> price.getId().equals(priceId))
                    .findFirst()
                    .ifPresent(price -> price.activationFailed(reason));
        }

    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class FeatureFacade {
        private Plan plan;

        /**
         * Adds a feature to the plan.
         * 
         * @param feature The feature to add.
         * @param value   The value of the feature.
         * @throws PlanDomainException If the feature already exists in the plan.
         */
        public void add(Feature feature, String value) throws PlanDomainException {
            if (feature == null) {
                throw new InvalidInputException("Feature cannot be null");
            }

            ensureFeatureManagementAllowed("add");

            if (this.plan.hasFeature(feature)) {
                throw PlanDomainException.duplicateFeature(feature.getCode());
            }

            PlanFeature planFeature = PlanFeature.create(plan, feature, value);
            this.plan.features.add(planFeature);
        }

        private boolean planHasAnyFeatureExcept(UUID featureId) {
            return plan.features.stream().anyMatch(f -> !f.getId().equals(featureId));
        }

        /**
         * Deletes a feature from the plan.
         * 
         * @param featureId The ID of the feature to delete.
         */
        public void delete(UUID featureId) throws PlanDomainException {
            if (featureId == null) {
                throw new InvalidInputException("Feature ID cannot be null");
            }
            ensureFeatureManagementAllowed("delete");
            if (plan.isActive() && !planHasAnyFeatureExcept(featureId)) {
                throw PlanDomainException.illegalOperation("Active plan must have at least one feature.");
            }
            plan.features.removeIf(f -> f.getId().equals(featureId));
        }

        private void ensureFeatureManagementAllowed(String todo) {
            if (!plan.isActive() && !plan.isDraft()) {
                throw PlanDomainException
                        .illegalOperation("Plan must be in ACTIVE or DRAFT status to {} a feature " + todo);
            }
        }

        /**
         * Updates the value of a feature in the plan.
         * 
         * @param featureId The ID of the feature to update.
         * @param value     The new value for the feature.
         * @throws PlanDomainException If the feature is not found.
         */
        public void updateValue(UUID featureId, String value) throws PlanDomainException {
            ensureFeatureManagementAllowed("update");
            plan.getPlanFeature(featureId).ifPresent(feat -> feat.setValue(value));
        }

        public PlanFeature getLast() {
            return plan.features.getLast();
        }

    }
}
