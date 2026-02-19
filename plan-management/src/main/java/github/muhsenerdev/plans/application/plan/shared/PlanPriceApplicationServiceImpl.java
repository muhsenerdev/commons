package github.muhsenerdev.plans.application.plan.shared;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import github.muhsenerdev.plans.application.plan.price.activate_price.ActivatePriceCommand;
import github.muhsenerdev.plans.application.plan.price.activate_price.ActivatePriceCommandHandler;
import github.muhsenerdev.plans.application.plan.price.add_price.AddPriceCommand;
import github.muhsenerdev.plans.application.plan.price.add_price.AddPriceCommandHandler;
import github.muhsenerdev.plans.application.plan.price.add_price.AddPriceResponse;
import github.muhsenerdev.plans.application.plan.price.archive.ArchivePriceCommand;
import github.muhsenerdev.plans.application.plan.price.archive.ArchivePriceCommandHandler;
import github.muhsenerdev.plans.application.plan.price.delete_price.DeletePriceCommand;
import github.muhsenerdev.plans.application.plan.price.delete_price.DeletePriceCommandHandler;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Validated
public class PlanPriceApplicationServiceImpl implements PlanPriceApplicationService {

    private final AddPriceCommandHandler addPriceCommandHandler;
    private final DeletePriceCommandHandler deletePriceCommandHandler;
    private final ActivatePriceCommandHandler activatePriceCommandHandler;
    private final ArchivePriceCommandHandler archivePriceCommandHandler;

    @Override
    public AddPriceResponse addPrice(@Valid AddPriceCommand command) {
        return addPriceCommandHandler.handle(command);
    }

    @Override
    public void deletePrice(@Valid DeletePriceCommand command) {
        deletePriceCommandHandler.handle(command);
    }

    @Override
    public void activatePrice(@Valid ActivatePriceCommand command) {
        activatePriceCommandHandler.handle(command);
    }

    @Override
    public void archivePrice(@Valid ArchivePriceCommand command) {
        archivePriceCommandHandler.handle(command);
    }

}
