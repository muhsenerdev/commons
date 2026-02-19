package github.muhsenerdev.plans.application.plan.price.activate_price;

import github.muhsenerdev.plans.application.plan.shared.PaymentGateway;
import github.muhsenerdev.plans.application.plan.shared.PlanMapper;
import github.muhsenerdev.plans.application.plan.shared.PlanService;
import github.muhsenerdev.plans.application.plan.shared.PricePaymentDto;
import github.muhsenerdev.plans.domain.plan.Plan;
import github.muhsenerdev.plans.domain.plan.PlanPrice;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class ActivatePriceCommandHandler {

    private final PlanService planService;
    private final PaymentGateway paymentGateway;
    private final PlanMapper planMapper;

    /**
     * This handler is not @Transactional because it interacts with an external
     * payment gateway.
     * Transactional steps are delegated to PlanService.
     */
    public void handle(ActivatePriceCommand command) {
        UUID planId = command.getPlanId();
        UUID priceId = command.getPriceId();
        log.info("Starting activation for price {} in plan {}", priceId, planId);

        Plan plan;
        try {
            // Step 1: Reserve for activation (Transactional)
            plan = planService.reservePriceForActivation(planId, priceId, command.isOverrideActivePrice());
        } catch (Exception e) {
            log.error("Failed to reserve price {} for activation in plan {}: {}", priceId, planId, e.getMessage());
            throw e;
        }

        try {
            // Step 2: Interact with Payment Gateway (External)
            PlanPrice price = plan.getPrices().stream()
                    .filter(p -> p.getId().equals(priceId))
                    .findFirst()
                    .orElseThrow();

            PricePaymentDto priceDto = planMapper.toPricePaymentDto(price);
            PricePaymentDto updatedPriceDto = paymentGateway.createPrice(priceDto, plan.getStripeProductId());

            // Step 3: Finalize activation (Transactional)
            planService.finalizePriceActivation(planId, priceId, updatedPriceDto.providerId());
            log.info("Successfully activated price {} in plan {}", priceId, planId);

        } catch (Exception e) {
            log.error("Failed to activate price {} via gateway for plan {}. Marking as failed.", priceId, planId, e);
            // Step 4: Mark as failed (Transactional)
            planService.markPriceAsFailed(planId, priceId, e.getMessage());
            throw e;
        }
    }
}
