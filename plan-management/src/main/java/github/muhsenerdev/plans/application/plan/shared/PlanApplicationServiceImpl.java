package github.muhsenerdev.plans.application.plan.shared;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import github.muhsenerdev.plans.application.plan.activate.ActivatePlanCommand;
import github.muhsenerdev.plans.application.plan.activate.ActivatePlanCommandHandler;
import github.muhsenerdev.plans.application.plan.create.CreatePlanCommand;
import github.muhsenerdev.plans.application.plan.create.CreatePlanCommandHandler;
import github.muhsenerdev.plans.application.plan.create.PlanCreationResponse;
import github.muhsenerdev.plans.application.plan.delete.DeletePlanCommand;
import github.muhsenerdev.plans.application.plan.delete.DeletePlanCommandHandler;
import github.muhsenerdev.plans.application.plan.update.UpdatePlanCommand;
import github.muhsenerdev.plans.application.plan.update.UpdatePlanCommandHandler;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Validated
public class PlanApplicationServiceImpl implements PlanApplicationService {

    private final CreatePlanCommandHandler createPlanCommandHandler;
    private final UpdatePlanCommandHandler updatePlanCommandHandler;
    private final ActivatePlanCommandHandler activatePlanCommandHandler;
    private final DeletePlanCommandHandler deletePlanCommandHandler;

    @Override
    public PlanCreationResponse createPlan(CreatePlanCommand command) {
        return createPlanCommandHandler.handle(command);
    }

    @Override
    public void updatePlan(UpdatePlanCommand command) {
        updatePlanCommandHandler.handle(command);
    }

    @Override
    public void activatePlan(ActivatePlanCommand command) {
        activatePlanCommandHandler.handle(command);
    }

    @Override
    public void deletePlan(DeletePlanCommand command) {
        deletePlanCommandHandler.handle(command);
    }

}
