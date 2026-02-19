package github.muhsenerdev.plans.web.admin;

import java.util.UUID;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import github.muhsenerdev.plans.application.plan.price.activate_price.ActivatePriceCommand;
import github.muhsenerdev.plans.application.plan.price.add_price.AddPriceCommand;
import github.muhsenerdev.plans.application.plan.price.add_price.AddPriceResponse;
import github.muhsenerdev.plans.application.plan.price.archive.ArchivePriceCommand;
import github.muhsenerdev.plans.application.plan.price.delete_price.DeletePriceCommand;
import github.muhsenerdev.plans.application.plan.shared.PlanPriceApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin/plans/{id}/prices")
@RequiredArgsConstructor
@Tag(name = "Plan Price Admin", description = "Endpoints for plan price administration")
public class PlanPriceAdminController {

    private final PlanPriceApplicationService planPriceApplicationService;

    @PostMapping
    @Operation(summary = "Add a price to a plan", description = "Allows an administrator to add a price to an existing plan")
    public AddPriceResponse addPrice(@PathVariable UUID id, @Valid @RequestBody AddPriceCommand command) {
        command.setId(id);
        return planPriceApplicationService.addPrice(command);
    }

    @DeleteMapping("/{priceId}")
    @Operation(summary = "Delete a price from a plan", description = "Allows an administrator to delete a price from an existing plan if it is in DRAFT status")
    public void deletePrice(@PathVariable UUID id, @PathVariable UUID priceId) {
        DeletePriceCommand command = DeletePriceCommand.builder()
                .planId(id)
                .priceId(priceId)
                .build();
        planPriceApplicationService.deletePrice(command);
    }

    @PostMapping("/{priceId}/activate")
    @Operation(summary = "Activate a plan price", description = "Allows an administrator to initiate the plan price activation process (non-transactional due to external gateway interaction)")
    public void activatePrice(@PathVariable UUID id, @PathVariable UUID priceId,
            @RequestParam(defaultValue = "false") boolean overrideActivePrice) {
        ActivatePriceCommand command = ActivatePriceCommand
                .builder()
                .planId(id)
                .priceId(priceId)
                .overrideActivePrice(overrideActivePrice)
                .build();
        planPriceApplicationService.activatePrice(command);
    }

    @PostMapping("/{priceId}/archive")
    @Operation(summary = "Archive a plan price", description = "Allows an administrator to archive an active plan price")
    public void archivePrice(@PathVariable UUID id, @PathVariable UUID priceId) {
        ArchivePriceCommand command = ArchivePriceCommand
                .builder()
                .planId(id)
                .priceId(priceId)
                .build();
        planPriceApplicationService.archivePrice(command);
    }

}
