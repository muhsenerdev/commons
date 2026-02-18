package github.muhsenerdev.plans.application.plan.shared;

import github.muhsenerdev.plans.application.plan.create.CreatePlanCommand;
import github.muhsenerdev.plans.application.plan.create.PlanCreationResponse;
import github.muhsenerdev.plans.application.plan.price.add_price.AddPriceCommand;
import github.muhsenerdev.plans.application.plan.price.add_price.AddPriceResponse;
import github.muhsenerdev.plans.application.plan.price.delete_price.DeletePriceCommand;
import github.muhsenerdev.plans.application.plan.price.update_price.UpdatePriceCommand;
import github.muhsenerdev.plans.application.plan.update.UpdatePlanCommand;
import jakarta.validation.Valid;

public interface PlanApplicationService {

    PlanCreationResponse createPlan(@Valid CreatePlanCommand command);

    void updatePlan(@Valid UpdatePlanCommand command);

    AddPriceResponse addPrice(@Valid AddPriceCommand command);

    void deletePrice(@Valid DeletePriceCommand command);

    void updatePrice(@Valid UpdatePriceCommand command);
}
