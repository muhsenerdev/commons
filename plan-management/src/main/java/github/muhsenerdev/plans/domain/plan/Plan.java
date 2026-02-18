package github.muhsenerdev.plans.domain.plan;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
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

    // public PlanPrice addPrice(Money price, Interval interval) {
    // if (isFree()) {
    // throw PlanDomainException.priceCannotBeAddedToFreePlan(this.getId());
    // }
    // PlanPrice planPrice = PlanPrice.builder()
    // .plan(this)
    // .price(price)
    // .interval(interval)
    // .build();
    // this.prices.add(planPrice);
    // return planPrice;
    // }

    public Optional<PlanPrice> getLastPrice() {
        return this.prices.isEmpty() ? Optional.empty() : Optional.of(this.prices.getLast());
    }

    public List<String> canPlanBeActivated(Set<UUID> priceIds) {
        if (status != PlanStatus.DRAFT && status != PlanStatus.ARCHIVED) {
            return List.of("Plan is not in draft or active status");
        }

        if (!hasAnyActiveFeature()) {
            return List.of("Plan must have at least one feature");
        }
        if (isFree()) {
            return List.of();
        }

        if (priceIds == null || priceIds.isEmpty()) {
            return List.of("To active paid plan, priceIds cannot be null or empty");
        }

        if (this.prices.isEmpty()) {
            return List.of("Paid plan has no prices");
        }

        List<String> errors = new ArrayList<>();
        Set<Interval> seenIntervals = new HashSet<>();
        List<PlanPrice> pricesToActivate = this.prices.stream()
                .filter(price -> priceIds.contains(price.getId()))
                .map(price -> {
                    Interval priceInterval = price.getPriceInterval();
                    if (seenIntervals.contains(priceInterval)) {
                        errors.add("Duplicate prices detected on interval: " + priceInterval);
                    } else {
                        seenIntervals.add(priceInterval);
                    }
                    return price;
                }).toList();
        if (!errors.isEmpty()) {
            return errors;
        }

        if (pricesToActivate.isEmpty()) {
            return List.of("No valid prices found to activate");
        }

        return List.of();
    }

    public void activatePlan(String stripeProductId, Map<UUID, String> priceIdToStripeIdMap) {

        List<String> errors = canPlanBeActivated(priceIdToStripeIdMap.keySet());
        if (!errors.isEmpty()) {
            throw PlanDomainException.cannotBeActivated(errors.get(0));
        }

        if (isFree()) {
            this.status = PlanStatus.ACTIVE;
            return;
        }

        if (priceIdToStripeIdMap == null || priceIdToStripeIdMap.isEmpty()
                || priceIdToStripeIdMap.containsValue(null)) {
            throw new InvalidInputException("To active paid plan, priceId and stripe Id map cannot be null or empty");
        }

        this.prices.stream()
                .filter(price -> priceIdToStripeIdMap.containsKey(price.getId()))
                .forEach(price -> price.activate(priceIdToStripeIdMap.get(price.getId())));
        this.status = PlanStatus.ACTIVE;
        this.stripeProductId = stripeProductId;
    }

    @JsonIgnore
    public boolean isFree() {
        return this.type == PlanType.FREE;
    }

    public void updateBasics(String name, String title, String description, int tier) {
        this.name = name;
        this.title = title;
        this.description = description;
        this.tier = tier;
        validate();
    }

    public void updateFull(PlanInput input) {
        if (this.status != PlanStatus.DRAFT) {
            throw PlanDomainException.cannotBeUpdated("Plan is not in draft status");
        }
        this.name = input.name();
        this.title = input.title();
        this.description = input.description();
        this.type = input.type();
        this.code = input.code();
        this.tier = input.tier();
        validate();
    }

    // public void updateFull(String name, String title, String description,
    // PlanType planType, String code, int tier) {
    // if (this.status != PlanStatus.DRAFT) {
    // throw PlanDomainException.cannotBeUpdated("Plan is not in draft status");
    // }
    // this.name = name;
    // this.title = title;
    // this.description = description;
    // this.type = planType;
    // this.code = code;
    // this.tier = tier;
    // validate();
    // }

    // public void updatePrice(UUID priceId, Money price, Interval interval) {
    // PlanPrice planPrice = this.prices.stream()
    // .filter(p -> p.getId().equals(priceId))
    // .findFirst()
    // .orElseThrow(() -> new InvalidInputException("Plan price not found with id: "
    // + priceId));
    // planPrice.update(price, interval);

    // }

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

    private boolean hasAnyActiveFeature() {
        return this.features.stream()
                .anyMatch(feature -> feature.getStatus() == FeatureStatus.ACTIVE);
    }

    public boolean isActive() {
        return this.status == PlanStatus.ACTIVE;
    }

    public boolean existsPriceById(UUID targetPriceId) {
        return this.prices.stream()
                .anyMatch(price -> price.getId().equals(targetPriceId));
    }

    // ========== ACTIVATE PLAN PRICE ==========
    public void checkPriceCanBeActivated(UUID priceId) {
        if (!isActive()) {
            throw PlanDomainException.cannotBeActivated("Plan must be ACTIVE to activate a price");
        }

        PlanPrice targetPrice = this.prices.stream()
                .filter(price -> price.getId().equals(priceId))
                .findFirst()
                .orElseThrow(() -> new InvalidInputException("Plan price not found: " + priceId));

        if (targetPrice.getStatus() != PriceStatus.DRAFT) {
            throw PlanDomainException.cannotBeActivated("Plan price must be in DRAFT status to be activated");
        }

        boolean activePriceExistsForSameInterval = this.prices.stream()
                .filter(price -> price.isActive() && price.getPriceInterval().equals(targetPrice.getPriceInterval()))
                .anyMatch(price -> !price.getId().equals(priceId));

        if (activePriceExistsForSameInterval) {
            throw PlanDomainException.cannotBeActivated(
                    "An active price already exists for interval: " + targetPrice.getPriceInterval());
        }
    }

    public void activatePrice(UUID priceId, String stripePriceId) {
        checkPriceCanBeActivated(priceId);
        this.prices.stream()
                .filter(price -> price.getId().equals(priceId))
                .forEach(price -> price.activate(stripePriceId));
    }

    public boolean arePlanAndPriceActive(UUID priceId) {
        return this.status == PlanStatus.ACTIVE && this.prices.stream()
                .filter(price -> price.getId().equals(priceId))
                .findFirst()
                .map(PlanPrice::isActive)
                .orElse(false);
    }

    public void delete() {
        if (this.status != PlanStatus.DRAFT) {
            throw PlanDomainException.planCannotBeDeleted("Only plans in DRAFT status can be deleted");
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
        if (status == PriceStatus.ARCHIVED)
            return;
        boolean hasPriceOnTheSameInterval = this.hasPrice(interval, status);
        if (hasPriceOnTheSameInterval) {
            throw new PlanDomainException("plan.duplicate_price",
                    "Plan has already prices on the same interval: {} in status: {}", interval, status);
        }
    }

    private boolean hasPrice(Interval interval, PriceStatus draft) {
        return this.prices.stream()
                .filter(price -> price.getPriceInterval().equals(interval))
                .filter(price -> price.getStatus().equals(draft))
                .findFirst()
                .isPresent();
    }

    private boolean hasFeature(Feature feature) {
        return this.features.stream()
                .filter(f -> f.getFeature().getId().equals(feature.getId()))
                .findFirst()
                .isPresent();
    }

    private PlanFeature getFeatureOrThrow(UUID featureId) {
        return this.features.stream()
                .filter(f -> f.getFeature().getId().equals(featureId))
                .findFirst()
                .orElseThrow(() -> PlanDomainException.featureNotFound(featureId));
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class PriceFacade {

        private Plan plan;

        public PlanPrice add(Money price, Interval interval) {
            if (plan.isFree()) {
                throw PlanDomainException.priceCannotBeAddedToFreePlan(plan.getId());
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
            Optional<PlanPrice> priceOpt = plan.prices.stream()
                    .filter(p -> p.getId().equals(priceId))
                    .findFirst();

            if (priceOpt.isEmpty()) {
                throw PlanDomainException.priceNotFound(priceId);
            }

            PlanPrice price = priceOpt.get();
            if (!price.isDraft()) {
                throw PlanDomainException.priceCannotBeDeleted("Only prices in DRAFT status can be deleted");
            }

            plan.prices.remove(price);
        }

        public void update(UUID priceId, Money price, Interval newInterval) {
            PlanPrice planPrice = plan.prices.stream()
                    .filter(p -> p.getId().equals(priceId))
                    .findFirst()
                    .orElseThrow(() -> PlanDomainException.priceNotFound(priceId));

            Interval priceOldInterval = planPrice.getPriceInterval();
            if (!Objects.equals(priceOldInterval, newInterval)) {
                plan.ensurePriceUniquenessOn(newInterval, planPrice.getStatus());
            }
            planPrice.update(price, newInterval);

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

            if (this.plan.hasFeature(feature)) {
                throw PlanDomainException.duplicateFeature(feature.getCode());
            }

            PlanFeature planFeature = PlanFeature.create(plan, feature, value);
            this.plan.features.add(planFeature);
        }

        /**
         * Deletes a feature from the plan.
         * 
         * @param featureId The ID of the feature to delete.
         */
        public void delete(UUID featureId) throws PlanDomainException {
            plan.features.removeIf(f -> f.getId().equals(featureId));
        }

        /**
         * Updates the value of a feature in the plan.
         * 
         * @param featureId The ID of the feature to update.
         * @param value     The new value for the feature.
         * @throws PlanDomainException If the feature is not found.
         */
        public void updateValue(UUID featureId, String value) throws PlanDomainException {
            PlanFeature feature = plan.getFeatureOrThrow(featureId);
            feature.setValue(value);
        }

    }
}
