package github.muhsenerdev.plans.domain.shared;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import github.muhsenerdev.plans.domain.plan.PlanFeature;
import github.muhsenerdev.plans.domain.subscription.Subscription.EntitlementsSnapshot;

public class EntitlementUtil {

    public static Map<String, EntitlementsSnapshot> extractEntitlements(List<PlanFeature> planFeatures) {
        Map<String, EntitlementsSnapshot> map = new HashMap<>();
        for (PlanFeature feat : planFeatures) {
            var ent = EntitlementsSnapshot.builder()
                    .value(feat.getValue())
                    .type(feat.getFeature().getType())
                    .build();
            map.put(feat.getFeature().getCode(), ent);
        }

        return map;
    }
}
