package github.muhsenerdev.plans.application.plan.shared;

import github.muhsenerdev.plans.application.plan.price.activate_price.ActivatePriceCommand;
import github.muhsenerdev.plans.application.plan.price.add_price.AddPriceCommand;
import github.muhsenerdev.plans.application.plan.price.add_price.AddPriceResponse;
import github.muhsenerdev.plans.application.plan.price.archive.ArchivePriceCommand;
import github.muhsenerdev.plans.application.plan.price.delete_price.DeletePriceCommand;
import jakarta.validation.Valid;

public interface PlanPriceApplicationService {

    AddPriceResponse addPrice(@Valid AddPriceCommand command);

    void deletePrice(@Valid DeletePriceCommand command);

    void activatePrice(@Valid ActivatePriceCommand command);

    void archivePrice(@Valid ArchivePriceCommand command);

}
