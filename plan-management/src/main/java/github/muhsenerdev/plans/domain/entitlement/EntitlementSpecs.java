// package github.muhsenerdev.plans.domain.entitlement;

// import java.time.LocalTime;
// import java.time.OffsetDateTime;
// import java.time.ZoneOffset;

// import org.springframework.data.jpa.domain.Specification;

// import
// com.github.muhsenerdev.langpra.plans.domain.subscription.SubscriptionFilter.DateRange;

// import jakarta.persistence.criteria.CriteriaBuilder;
// import jakarta.persistence.criteria.Predicate;
// import jakarta.persistence.criteria.Root;

// public class EntitlementSpecs {

// public static Specification<Entitlement> withFilter(EntitlementFilter filter)
// {
// return (root, query, cb) -> {
// Predicate p = cb.conjunction();

// if (filter.userId() != null) {
// p = cb.and(p, cb.equal(root.get("userId"), filter.userId()));
// }

// if (filter.featureCode() != null) {
// p = cb.and(p, cb.equal(root.get("featureCode"), filter.featureCode()));
// }

// if (filter.sourceType() != null) {
// p = cb.and(p, cb.equal(root.get("sourceType"), filter.sourceType()));
// }

// if (filter.status() != null) {
// p = cb.and(p, cb.equal(root.get("status"), filter.status()));
// }

// if (filter.validFrom() != null) {
// p = cb.and(p, createDateRangePredicate(cb, root, "validFrom",
// filter.validFrom()));
// }

// if (filter.validTo() != null) {
// p = cb.and(p, createDateRangePredicate(cb, root, "validTo",
// filter.validTo()));
// }

// return p;
// };
// }

// private static Predicate createDateRangePredicate(CriteriaBuilder cb,
// Root<Entitlement> root, String fieldName,
// DateRange dateRange) {
// ZoneOffset utc = ZoneOffset.UTC;

// if (dateRange.end() == null) {
// // Single day filter
// OffsetDateTime startOfDay = dateRange.start().atStartOfDay().atOffset(utc);
// OffsetDateTime endOfDay =
// dateRange.start().atTime(LocalTime.MAX).atOffset(utc);
// return cb.between(root.get(fieldName), startOfDay, endOfDay);
// } else {
// // Date range filter
// OffsetDateTime startRange = dateRange.start().atStartOfDay().atOffset(utc);
// OffsetDateTime endRange =
// dateRange.end().atTime(LocalTime.MAX).atOffset(utc);
// return cb.between(root.get(fieldName), startRange, endRange);
// }
// }
// }
