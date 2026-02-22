package github.muhsenerdev.plans.application.shared;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import github.muhsenerdev.plans.application.plan.shared.PlanService;
import github.muhsenerdev.plans.domain.plan.Plan;
import github.muhsenerdev.plans.domain.shared.EntitlementUtil;
import github.muhsenerdev.plans.domain.subscription.MaintenanceMode;
import github.muhsenerdev.plans.domain.subscription.Subscription;
import github.muhsenerdev.plans.domain.subscription.SubscriptionRepository;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SubscriptionAutoMaintenanceService {

    private final EntitlementService entitlementService;
    private final PlanService planService;
    private final SubscriptionRepository subscriptionRepository;

    @Transactional
    public void doAutoMaintenance(Subscription subscription) {

        MaintenanceMode maintenanceMode = subscription.getMaintenanceMode();
        if (maintenanceMode == MaintenanceMode.FROM_PLAN) {
            Plan plan = planService.findWithFeaturesOrThrow(subscription.getPlanId());

            var entitlementSnaphots = EntitlementUtil.extractEntitlements(plan.getFeatures());
            subscription.doAutoMaintenance(entitlementSnaphots);
            entitlementService.grantEntitlements(subscription);

        } else if (maintenanceMode == MaintenanceMode.FROM_SNAPSHOT) {
            entitlementService.grantEntitlements(subscription);
        }

        subscriptionRepository.save(subscription);

    }

}
