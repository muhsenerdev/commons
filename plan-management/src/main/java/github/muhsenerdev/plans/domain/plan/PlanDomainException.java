package github.muhsenerdev.plans.domain.plan;

import java.util.UUID;

import org.antlr.v4.runtime.misc.Interval;

import github.muhsenerdev.commons.core.exception.DomainException;

public class PlanDomainException extends DomainException {

    protected PlanDomainException(String code, String message, Object... args) {
        super(code, message, args);
    }

    protected PlanDomainException(String message, Object... args) {
        super("plan.error", message, args);
    }

    public static PlanDomainException duplicatePriceOnInterval(Interval interval) {
        return new PlanDomainException("plan.duplicate-prices", "Duplicate prices detected on interval: {}", interval);
    }

    public static PlanDomainException cannotBeActivated(String reason) {
        return new PlanDomainException("plan.cannot-be-activated", "Plan cannot be activated: {}", reason);
    }

    public static PlanDomainException priceCannotBeActivated(String reason) {
        return new PlanDomainException("plan.price.cannot-be-activated", "Plan price cannot be activated: {}", reason);
    }

    public static PlanDomainException featureCannotBeArchived(String reason) {
        return new PlanDomainException("plan.feature.cannot-be-archived", "Plan feature cannot be archived: {}",
                reason);
    }

    public static PlanDomainException duplicateFeature(String code) {
        return new PlanDomainException("plan.feature.duplicate", "Plan has already feature with code: {}", code);
    }

    public static PlanDomainException cannotActivateDiscount(String reason) {
        return new PlanDomainException("plan.discount.cannot-be-activated", "Discount cannot be activated: {}", reason);
    }

    public static PlanDomainException cannotPassiveDiscount(String reason) {
        return new PlanDomainException("plan.discount.cannot-be-passived", "Discount cannot be passived: {}", reason);
    }

    public static PlanDomainException cannotUpdateDiscount(String reason) {
        return new PlanDomainException("plan.discount.cannot-be-updated", "Discount cannot be updated: {}", reason);
    }

    public static PlanDomainException cannotBeUpdated(String reason) {
        return new PlanDomainException("plan.cannot-be-updated", "Plan cannot be updated: {}", reason);
    }

    public static PlanDomainException priceCannotBeUpdated(String reason) {
        return new PlanDomainException("plan.price.cannot-be-updated", "Plan price cannot be updated: {}", reason);
    }

    public static PlanDomainException priceCannotBeDeleted(String reason) {
        return new PlanDomainException("plan.price.cannot-be-deleted", "Plan price cannot be deleted: {}", reason);
    }

    public static PlanDomainException priceCannotBeAddedToFreePlan(UUID id) {
        return new PlanDomainException("plan.price.cannot-be-added-to-free-plan",
                "Plan price cannot be added to free plan: {}", id);
    }

    public static PlanDomainException planCannotBeDeleted(String reason) {
        return new PlanDomainException("plan.cannot-be-deleted", "Plan cannot be deleted: {}", reason);
    }

    public static PlanDomainException codeMustBeUnique(String code) {
        return new PlanDomainException("plan.code.unique", "Plan code must be unique: {}", code);
    }

    public static PlanDomainException tierMustBeUnique(int tier) {
        return new PlanDomainException("plan.tier.unique", "Plan tier must be unique: {}", tier);
    }

    public static PlanDomainException cannotBeArchived(String reason) {
        return new PlanDomainException("plan.cannot-be-archived", "Plan cannot be archived: {}", reason);
    }

    public static PlanDomainException priceNotFound(UUID priceId) {
        return new PlanDomainException("plan.price_not_found", "Plan price not found with id: {}", priceId);
    }

    public static PlanDomainException featureNotFound(UUID featureId) {
        return new PlanDomainException("plan.feature_not_found", "Plan feature not found with id: {}", featureId);
    }

}
