package github.muhsenerdev.plans.application.plan.shared;

public interface PaymentGateway {
    /**
     * Creates or updates the plan and its prices in the payment provider (e.g.,
     * Stripe).
     * 
     * @param planPaymentDto The plan and price details.
     * @return The updated DTO with provider IDs (product ID and price IDs)
     *         populated.
     */
    PlanPaymentDto createPlanAndPrices(PlanPaymentDto planPaymentDto);

    PricePaymentDto createPrice(PricePaymentDto pricePaymentDto, String planProviderId);

    CheckoutInfo startCheckout(CheckoutRequest request);
}
