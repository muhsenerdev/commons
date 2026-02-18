package github.muhsenerdev.plans.web.admin;

import java.util.UUID;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import github.muhsenerdev.plans.application.plan.create.CreatePlanCommand;
import github.muhsenerdev.plans.application.plan.create.PlanCreationResponse;
import github.muhsenerdev.plans.application.plan.price.add_price.AddPriceCommand;
import github.muhsenerdev.plans.application.plan.price.add_price.AddPriceResponse;
import github.muhsenerdev.plans.application.plan.price.delete_price.DeletePriceCommand;
import github.muhsenerdev.plans.application.plan.price.update_price.UpdatePriceCommand;
import github.muhsenerdev.plans.application.plan.update.UpdatePlanCommand;
import github.muhsenerdev.plans.application.plan.shared.PlanApplicationService;
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

    @PostMapping("/{id}/prices")
    @Operation(summary = "Add a price to a plan", description = "Allows an administrator to add a price to an existing plan")
    public AddPriceResponse addPrice(@PathVariable UUID id, @Valid @RequestBody AddPriceCommand command) {
        command.setId(id);
        return planApplicationService.addPrice(command);
    }

    @PutMapping("/{id}/prices/{priceId}")
    @Operation(summary = "Update a price in a plan", description = "Allows an administrator to update a price in an existing plan if it is in DRAFT status")
    public void updatePrice(@PathVariable UUID id, @PathVariable UUID priceId,
            @Valid @RequestBody UpdatePriceCommand command) {
        command.setPlanId(id);
        command.setPriceId(priceId);
        planApplicationService.updatePrice(command);
    }

    @DeleteMapping("/{id}/prices/{priceId}")
    @Operation(summary = "Delete a price from a plan", description = "Allows an administrator to delete a price from an existing plan if it is in DRAFT status")
    public void deletePrice(@PathVariable UUID id, @PathVariable UUID priceId) {
        DeletePriceCommand command = DeletePriceCommand.builder()
                .planId(id)
                .priceId(priceId)
                .build();
        planApplicationService.deletePrice(command);
    }
}
