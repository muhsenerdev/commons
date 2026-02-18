package github.muhsenerdev.plans.infra.adapter.payment;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Component;

import github.muhsenerdev.plans.application.plan.shared.PaymentGateway;
import github.muhsenerdev.plans.application.plan.shared.PlanPaymentDto;
import github.muhsenerdev.plans.application.plan.shared.PricePaymentDto;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class PaymentGatewayImpl implements PaymentGateway {

    @Override
    public PlanPaymentDto createPlanAndPrices(PlanPaymentDto planPaymentDto) {
        log.info("Creating plan and prices for plan: {}", planPaymentDto);
        List<PricePaymentDto> pricePaymentDtos = planPaymentDto.prices().stream().map(pricePaymentDto -> {
            return pricePaymentDto.toBuilder().providerId("providerID-" + UUID.randomUUID()).build();
        }).toList();
        return planPaymentDto.toBuilder().providerId("providerID-" + UUID.randomUUID()).prices(pricePaymentDtos)
                .build();
    }

}
