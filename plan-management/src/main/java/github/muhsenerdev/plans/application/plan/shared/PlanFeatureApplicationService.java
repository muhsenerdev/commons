package github.muhsenerdev.plans.application.plan.shared;

import github.muhsenerdev.plans.application.plan.feature.add.AddPlanFeatureCommand;
import github.muhsenerdev.plans.application.plan.feature.add.AddPlanFeatureResponse;
import github.muhsenerdev.plans.application.plan.feature.archive.DeletePlanFeatureCommand;
import github.muhsenerdev.plans.application.plan.feature.update_value.UpdatePlanFeatureValueCommand;
import jakarta.validation.Valid;

public interface PlanFeatureApplicationService {

    AddPlanFeatureResponse addFeature(@Valid AddPlanFeatureCommand command);

    void deleteFeature(@Valid DeletePlanFeatureCommand command);

    void updateFeatureValue(@Valid UpdatePlanFeatureValueCommand command);

}
