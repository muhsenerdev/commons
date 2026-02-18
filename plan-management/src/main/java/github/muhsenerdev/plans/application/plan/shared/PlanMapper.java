package github.muhsenerdev.plans.application.plan.shared;

import org.mapstruct.Mapper;

import github.muhsenerdev.commons.core.vo.CommonVoMapper;
import github.muhsenerdev.commons.core.vo.Money;
import github.muhsenerdev.plans.application.plan.create.CreatePlanCommand;
import github.muhsenerdev.plans.application.plan.price.add_price.AddPriceCommand;
import github.muhsenerdev.plans.application.plan.price.update_price.UpdatePriceCommand;
import github.muhsenerdev.plans.application.plan.update.UpdatePlanCommand;
import github.muhsenerdev.plans.application.shared.PlanModuleVoMapper;
import github.muhsenerdev.plans.domain.plan.PlanInput;

@Mapper(componentModel = "spring", uses = { CommonVoMapper.class, PlanModuleVoMapper.class })
public interface PlanMapper {

    PlanInput toCreationInput(CreatePlanCommand command);

    PlanInput toInput(UpdatePlanCommand command);

    Money extractMoney(AddPriceCommand command);

    Money extractMoney(UpdatePriceCommand command);
}
