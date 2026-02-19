package github.muhsenerdev.plans.application.plan.shared;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import github.muhsenerdev.plans.application.plan.feature.add.AddPlanFeatureCommand;
import github.muhsenerdev.plans.application.plan.feature.add.AddPlanFeatureCommandHandler;
import github.muhsenerdev.plans.application.plan.feature.add.AddPlanFeatureResponse;
import github.muhsenerdev.plans.application.plan.feature.archive.DeletePlanFeatureCommand;
import github.muhsenerdev.plans.application.plan.feature.archive.DeletePlanFeatureCommandHandler;
import github.muhsenerdev.plans.application.plan.feature.update_value.UpdatePlanFeatureValueCommand;
import github.muhsenerdev.plans.application.plan.feature.update_value.UpdatePlanFeatureValueCommandHandler;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Validated
public class PlanFeatureApplicationServiceImpl implements PlanFeatureApplicationService {

    private final AddPlanFeatureCommandHandler addPlanFeatureCommandHandler;
    private final DeletePlanFeatureCommandHandler deletePlanFeatureCommandHandler;
    private final UpdatePlanFeatureValueCommandHandler updatePlanFeatureValueCommandHandler;

    @Override
    public AddPlanFeatureResponse addFeature(@Valid AddPlanFeatureCommand command) {
        return addPlanFeatureCommandHandler.handle(command);
    }

    @Override
    public void deleteFeature(@Valid DeletePlanFeatureCommand command) {
        deletePlanFeatureCommandHandler.handle(command);
    }

    @Override
    public void updateFeatureValue(@Valid UpdatePlanFeatureValueCommand command) {
        updatePlanFeatureValueCommandHandler.handle(command);
    }

}
