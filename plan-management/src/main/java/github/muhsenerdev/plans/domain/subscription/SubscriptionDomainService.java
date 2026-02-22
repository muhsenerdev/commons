package github.muhsenerdev.plans.domain.subscription;

import java.util.UUID;

import org.springframework.stereotype.Service;

import github.muhsenerdev.plans.domain.plan.Plan;
import github.muhsenerdev.plans.domain.shared.EntitlementUtil;
import github.muhsenerdev.plans.domain.shared.Interval;

@Service
public class SubscriptionDomainService {

    /*
     * Returns PENDING status subscription
     */
    public Subscription subscribe(UUID userId, Plan plan, UUID priceId) {
        if (!plan.isActive()) {
            throw SubscriptionDomainException.planNotActive(plan.getId());
        }

        var priceOpt = plan.findPrice(priceId); // if null, returns empty optional

        Interval interval = null;
        if (plan.isPaid()) {
            if (priceOpt.isEmpty()) {
                throw SubscriptionDomainException.priceNotExists(priceId, plan.getId());
            }
            var price = priceOpt.get();
            if (!price.isActive()) {
                throw SubscriptionDomainException.priceNotActive(priceId);
            }

            interval = price.getPriceInterval();
        } else {
            priceId = null;
            interval = Interval.INFINITE;
        }

        return Subscription.builder()
                .userId(userId)
                .planId(plan.getId())
                .priceId(priceId) // null for free plans
                .interval(interval)
                .entitlements(EntitlementUtil.extractEntitlements(plan.getFeatures()))
                .build();
    }

}
