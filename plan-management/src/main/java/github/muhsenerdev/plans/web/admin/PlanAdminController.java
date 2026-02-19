package github.muhsenerdev.plans.web.admin;

import java.util.UUID;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import github.muhsenerdev.plans.application.plan.activate.ActivatePlanCommand;
import github.muhsenerdev.plans.application.plan.create.CreatePlanCommand;
import github.muhsenerdev.plans.application.plan.create.PlanCreationResponse;
import github.muhsenerdev.plans.application.plan.delete.DeletePlanCommand;
import github.muhsenerdev.plans.application.plan.shared.PlanApplicationService;
import github.muhsenerdev.plans.application.plan.update.UpdatePlanCommand;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin/plans")
@RequiredArgsConstructor
@Tag(name = "Plan Admin", description = "Endpoints for plan administration")
public class PlanAdminController {

    private final PlanApplicationService planApplicationService;

    @PostMapping
    @Operation(summary = "Create a new plan", description = "Allows an administrator to create a new plan")
    public PlanCreationResponse createPlan(@Valid @RequestBody CreatePlanCommand command) {
        return planApplicationService.createPlan(command);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing plan", description = "Allows an administrator to update an existing plan if it is in DRAFT status")
    public void updatePlan(@PathVariable UUID id, @Valid @RequestBody UpdatePlanCommand command) {
        command.setId(id);
        planApplicationService.updatePlan(command);
    }

    @PostMapping("/{id}/activate")
    @Operation(summary = "Activate a plan", description = "Allows an administrator to initiate the plan activation process (non-transactional due to external gateway interaction)")
    public void activatePlan(@PathVariable UUID id) {
        ActivatePlanCommand command = ActivatePlanCommand
                .builder()
                .planId(id)
                .build();
        planApplicationService.activatePlan(command);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a plan", description = "Allows an administrator to delete an existing plan if it is in DRAFT status")
    public void deletePlan(@PathVariable UUID id) {
        DeletePlanCommand command = DeletePlanCommand.builder()
                .planId(id)
                .build();
        planApplicationService.deletePlan(command);
    }
}
