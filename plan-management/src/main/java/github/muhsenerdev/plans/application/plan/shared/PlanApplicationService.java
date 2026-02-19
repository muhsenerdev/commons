package github.muhsenerdev.plans.application.plan.shared;

import github.muhsenerdev.plans.application.plan.activate.ActivatePlanCommand;
import github.muhsenerdev.plans.application.plan.archive.ArchievePlanCommand;
import github.muhsenerdev.plans.application.plan.create.CreatePlanCommand;
import github.muhsenerdev.plans.application.plan.create.PlanCreationResponse;
import github.muhsenerdev.plans.application.plan.delete.DeletePlanCommand;
import github.muhsenerdev.plans.application.plan.update.UpdatePlanCommand;
import jakarta.validation.Valid;

public interface PlanApplicationService {

    PlanCreationResponse createPlan(@Valid CreatePlanCommand command);

    void updatePlan(@Valid UpdatePlanCommand command);

    void activatePlan(@Valid ActivatePlanCommand command);

    void deletePlan(@Valid DeletePlanCommand command);

    void archivePlan(@Valid ArchievePlanCommand command);

}
