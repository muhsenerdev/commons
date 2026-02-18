package github.muhsenerdev.plans.web.admin;

import java.util.UUID;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import github.muhsenerdev.plans.application.plan.feature.add.AddPlanFeatureCommand;
import github.muhsenerdev.plans.application.plan.feature.add.AddPlanFeatureResponse;
import github.muhsenerdev.plans.application.plan.feature.archive.DeletePlanFeatureCommand;
import github.muhsenerdev.plans.application.plan.feature.update_value.UpdatePlanFeatureValueCommand;
import github.muhsenerdev.plans.application.plan.shared.PlanApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin/plans/{planId}/features")
@RequiredArgsConstructor
@Tag(name = "Plan Feature Admin", description = "Endpoints for managing features of a specific plan")
public class PlanFeatureAdminController {

    private final PlanApplicationService planApplicationService;

    @PostMapping("")
    @Operation(summary = "Add a feature to a plan", description = "Adds a global feature to the specified plan with a value")
    public AddPlanFeatureResponse addFeature(@PathVariable UUID planId,
            @Valid @RequestBody AddPlanFeatureCommand command) {
        command.setPlanId(planId);
        return planApplicationService.addFeature(command);
    }

    @DeleteMapping("/{planFeatureId}")
    @Operation(summary = "Delete a plan feature", description = "Deletes a feature associated with a plan")
    public void deleteFeature(@PathVariable UUID planId, @PathVariable UUID planFeatureId) {
        DeletePlanFeatureCommand command = DeletePlanFeatureCommand.builder()
                .planId(planId)
                .planFeatureId(planFeatureId)
                .build();
        planApplicationService.deleteFeature(command);
    }

    @PatchMapping("/{planFeatureId}")
    @Operation(summary = "Update plan feature value", description = "Updates the value of a feature in a plan")
    public void updateFeatureValue(@PathVariable UUID planId, @PathVariable UUID planFeatureId,
            @Valid @RequestBody UpdatePlanFeatureValueCommand command) {
        command.setPlanId(planId);
        command.setPlanFeatureId(planFeatureId);
        planApplicationService.updateFeatureValue(command);
    }

}
