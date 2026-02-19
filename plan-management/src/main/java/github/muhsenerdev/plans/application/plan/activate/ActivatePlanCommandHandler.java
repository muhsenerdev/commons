package github.muhsenerdev.plans.application.plan.activate;

import github.muhsenerdev.plans.application.plan.shared.PaymentGateway;
import github.muhsenerdev.plans.application.plan.shared.PlanMapper;
import github.muhsenerdev.plans.application.plan.shared.PlanPaymentDto;
import github.muhsenerdev.plans.application.plan.shared.PlanService;
import github.muhsenerdev.plans.application.plan.shared.PricePaymentDto;
import github.muhsenerdev.plans.domain.plan.Plan;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class ActivatePlanCommandHandler {

    private final PlanService planService;
    private final PaymentGateway paymentGateway;
    private final PlanMapper planMapper;

    /**
     * This handler is not @Transactional because it interacts with an external
     * payment gateway.
     * Transactional steps are delegated to PlanService.
     */
    public void handle(ActivatePlanCommand command) {
        UUID planId = command.getPlanId();
        log.debug("Starting activation for plan: {}", planId);

        Plan plan;
        try {
            // Step 1: Reserve for activation (Transactional)
            plan = planService.reserveForActivation(planId);
        } catch (Exception e) {
            log.error("Failed to reserve plan for activation: {}", planId, e);
            throw e;
        }

        if (plan.isFree()) {
            planService.finalizeActivation(planId, null, null);
            log.debug("Successfully activated free plan: {}", planId);
            return;
        }

        try {
            // Step 2: Interact with Payment Gateway (External)
            PlanPaymentDto paymentDto = planMapper.toPaymentDto(plan);
            PlanPaymentDto updatedPaymentDto = paymentGateway.createPlanAndPrices(paymentDto);

            // Step 3: Finalize activation (Transactional)
            String providerId = updatedPaymentDto.providerId();
            Map<UUID, String> priceProviderIds = updatedPaymentDto.prices().stream()
                    .collect(Collectors.toMap(PricePaymentDto::id, PricePaymentDto::providerId));

            planService.finalizeActivation(planId, providerId, priceProviderIds);
            log.debug("Successfully activated plan: {}", planId);

        } catch (Exception e) {
            log.debug("Failed to activate plan via gateway: {}. Marking as failed.", planId, e);
            // Step 4: Mark as failed (Transactional)
            planService.markAsFailed(planId, e.getMessage());
            throw e;
        }
    }
}
