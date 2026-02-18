package github.muhsenerdev.users.core.domain.users;

import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

public class UserSpecs {

    public static Specification<User> create(UserFilter filter) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (filter.getStatus() != null) {
                predicates.add(cb.equal(root.get("status"), filter.getStatus()));
            }

            if (filter.getVerificationStatus() != null) {
                predicates.add(cb.equal(root.get("emailVerification").get("status"), filter.getVerificationStatus()));
            }

            if (filter.getEmail() != null && !filter.getEmail().isBlank()) {
                predicates.add(
                        cb.like(cb.lower(root.get("email").get("email")), "%" + filter.getEmail().toLowerCase() + "%"));
            }

            if (filter.getRegistrationType() != null) {
                predicates.add(cb.equal(root.get("registrationType"), filter.getRegistrationType()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
