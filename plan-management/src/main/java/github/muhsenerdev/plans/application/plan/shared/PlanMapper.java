package github.muhsenerdev.plans.application.plan.shared;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import github.muhsenerdev.commons.core.vo.CommonVoMapper;
import github.muhsenerdev.commons.core.vo.Money;
import github.muhsenerdev.plans.application.plan.create.CreatePlanCommand;
import github.muhsenerdev.plans.application.plan.price.add_price.AddPriceCommand;
import github.muhsenerdev.plans.application.plan.update.UpdatePlanCommand;
import github.muhsenerdev.plans.application.shared.PlanModuleVoMapper;
import github.muhsenerdev.plans.domain.plan.Plan;
import github.muhsenerdev.plans.domain.plan.PlanInput;
import github.muhsenerdev.plans.domain.plan.PlanPrice;

@Mapper(componentModel = "spring", uses = { CommonVoMapper.class, PlanModuleVoMapper.class })
public interface PlanMapper {

    PlanInput toCreationInput(CreatePlanCommand command);

    PlanInput toInput(UpdatePlanCommand command);

    Money extractMoney(AddPriceCommand command);

    // Money extractMoney(UpdatePriceCommand command);

    @Mapping(target = "providerId", source = "stripeProductId")
    PlanPaymentDto toPaymentDto(Plan plan);

    @Mapping(target = "amount", source = "price.amount")
    @Mapping(target = "currency", source = "price.currency")
    @Mapping(target = "interval", source = "priceInterval")
    @Mapping(target = "providerId", source = "stripePriceId")
    PricePaymentDto toPricePaymentDto(PlanPrice price);
}
