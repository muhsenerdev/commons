package github.muhsenerdev.plans.domain.subscription;

import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public class SubscriptionSpecs {

    public static Specification<Subscription> withFilter(SubscriptionFilter filter) {
        return (root, query, cb) -> {
            Predicate p = cb.conjunction();

            if (filter.userId() != null) {
                p = cb.and(p, cb.equal(root.get("userId"), filter.userId()));
            }

            if (filter.planId() != null) {
                p = cb.and(p, cb.equal(root.get("planId"), filter.planId()));
            }

            if (filter.priceId() != null) {
                p = cb.and(p, cb.equal(root.get("priceId"), filter.priceId()));
            }

            if (filter.status() != null) {
                p = cb.and(p, cb.equal(root.get("status"), filter.status()));
            }

            if (filter.interval() != null) {
                p = cb.and(p, cb.equal(root.get("interval"), filter.interval()));
            }

            if (filter.currentPeriodStart() != null) {
                p = cb.and(p, createDateRangePredicate(cb, root, "currentPeriodStart", filter.currentPeriodStart()));
            }

            if (filter.createdAt() != null) {
                p = cb.and(p, createDateRangePredicate(cb, root, "createdAt", filter.createdAt()));
            }

            if (filter.currentPeriodEnd() != null) {
                p = cb.and(p, createDateRangePredicate(cb, root, "currentPeriodEnd", filter.currentPeriodEnd()));
            }

            return p;
        };
    }

    private static Predicate createDateRangePredicate(CriteriaBuilder cb, Root<Subscription> root, String fieldName,
            SubscriptionFilter.DateRange dateRange) {
        ZoneOffset utc = ZoneOffset.UTC;

        if (dateRange.end() == null) {
            // Single day filter
            OffsetDateTime startOfDay = dateRange.start().atStartOfDay().atOffset(utc);
            OffsetDateTime endOfDay = dateRange.start().atTime(LocalTime.MAX).atOffset(utc);
            return cb.between(root.get(fieldName), startOfDay, endOfDay);
        } else {
            // Date range filter
            OffsetDateTime startRange = dateRange.start().atStartOfDay().atOffset(utc);
            OffsetDateTime endRange = dateRange.end().atTime(LocalTime.MAX).atOffset(utc);
            return cb.between(root.get(fieldName), startRange, endRange);
        }
    }
}
