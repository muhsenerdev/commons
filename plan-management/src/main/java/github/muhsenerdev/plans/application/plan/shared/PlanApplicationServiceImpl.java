package github.muhsenerdev.plans.application.plan.shared;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import github.muhsenerdev.plans.application.plan.create.CreatePlanCommand;
import github.muhsenerdev.plans.application.plan.create.CreatePlanCommandHandler;
import github.muhsenerdev.plans.application.plan.create.PlanCreationResponse;
import github.muhsenerdev.plans.application.plan.price.add_price.AddPriceCommand;
import github.muhsenerdev.plans.application.plan.price.add_price.AddPriceCommandHandler;
import github.muhsenerdev.plans.application.plan.price.add_price.AddPriceResponse;
import github.muhsenerdev.plans.application.plan.price.delete_price.DeletePriceCommand;
import github.muhsenerdev.plans.application.plan.price.delete_price.DeletePriceCommandHandler;
import github.muhsenerdev.plans.application.plan.price.update_price.UpdatePriceCommand;
import github.muhsenerdev.plans.application.plan.price.update_price.UpdatePriceCommandHandler;
import github.muhsenerdev.plans.application.plan.update.UpdatePlanCommand;
import github.muhsenerdev.plans.application.plan.update.UpdatePlanCommandHandler;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Validated
public class PlanApplicationServiceImpl implements PlanApplicationService {

    private final CreatePlanCommandHandler createPlanCommandHandler;
    private final UpdatePlanCommandHandler updatePlanCommandHandler;
    private final AddPriceCommandHandler addPriceCommandHandler;
    private final DeletePriceCommandHandler deletePriceCommandHandler;
    private final UpdatePriceCommandHandler updatePriceCommandHandler;

    @Override
    public PlanCreationResponse createPlan(CreatePlanCommand command) {
        return createPlanCommandHandler.handle(command);
    }

    @Override
    public void updatePlan(UpdatePlanCommand command) {
        updatePlanCommandHandler.handle(command);
    }

    @Override
    public AddPriceResponse addPrice(AddPriceCommand command) {
        return addPriceCommandHandler.handle(command);
    }

    @Override
    public void deletePrice(DeletePriceCommand command) {
        deletePriceCommandHandler.handle(command);
    }

    @Override
    public void updatePrice(UpdatePriceCommand command) {
        updatePriceCommandHandler.handle(command);
    }
}
