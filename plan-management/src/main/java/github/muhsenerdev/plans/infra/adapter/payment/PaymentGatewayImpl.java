package github.muhsenerdev.plans.infra.adapter.payment;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.stripe.exception.StripeException;
import com.stripe.model.Product;
import com.stripe.model.Price;
import com.stripe.model.checkout.Session;
import com.stripe.param.ProductCreateParams;
import com.stripe.param.PriceCreateParams;
import com.stripe.param.checkout.SessionCreateParams;

import github.muhsenerdev.plans.application.plan.shared.CheckoutInfo;
import github.muhsenerdev.plans.application.plan.shared.CheckoutRequest;
import github.muhsenerdev.plans.application.plan.shared.PaymentGateway;
import github.muhsenerdev.plans.application.plan.shared.PlanPaymentDto;
import github.muhsenerdev.plans.application.plan.shared.PricePaymentDto;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class PaymentGatewayImpl implements PaymentGateway {

    @Value("${app.stripe.success-url}")
    private String successUrl;

    @Value("${app.stripe.cancel-url}")
    private String cancelUrl;

    @Override
    public PlanPaymentDto createPlanAndPrices(PlanPaymentDto planPaymentDto) {
        log.info("Creating plan and prices for plan: {}", planPaymentDto);
        try {
            // Create Stripe Product
            ProductCreateParams productParams = ProductCreateParams.builder()
                    .setName(planPaymentDto.name())
                    .setDescription(planPaymentDto.description())
                    .build();
            Product product = Product.create(productParams);
            String productId = product.getId();

            // Create Stripe Prices for the Product
            List<PricePaymentDto> pricePaymentDtos = planPaymentDto.prices().stream().map(priceDto -> {
                try {
                    PriceCreateParams.Recurring.Interval interval = null;
                    if (priceDto.interval().isMonthly()) {
                        interval = PriceCreateParams.Recurring.Interval.MONTH;
                    } else if (priceDto.interval().isYearly()) {
                        interval = PriceCreateParams.Recurring.Interval.YEAR;
                    }

                    PriceCreateParams.Builder priceParamsBuilder = PriceCreateParams.builder()
                            .setUnitAmountDecimal(priceDto.amount().multiply(new java.math.BigDecimal("100"))) // Stripe
                                                                                                               // amount
                                                                                                               // is in
                                                                                                               // cents
                            .setCurrency(priceDto.currency().toLowerCase())
                            .setProduct(productId);

                    if (interval != null) {
                        priceParamsBuilder.setRecurring(PriceCreateParams.Recurring.builder()
                                .setInterval(interval)
                                .build());
                    }

                    Price price = Price.create(priceParamsBuilder.build());
                    return priceDto.toBuilder().providerId(price.getId()).build();

                } catch (StripeException e) {
                    throw new RuntimeException("Failed to create Stripe price for plan: " + planPaymentDto.name(), e);
                }
            }).toList();

            return planPaymentDto.toBuilder()
                    .providerId(productId)
                    .prices(pricePaymentDtos)
                    .build();

        } catch (StripeException e) {
            log.error("Failed to create Stripe product/prices", e);
            throw new PaymentGatewayException("Failed to create Stripe product: " + e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error creating Stripe product/prices", e);
            throw new PaymentGatewayException("Unexpected error creating Stripe product");
        }
    }

    @Override
    public PricePaymentDto createPrice(PricePaymentDto pricePaymentDto, String planProviderId) {
        log.info("Creating price for plan {}: {}", planProviderId, pricePaymentDto);
        return pricePaymentDto.toBuilder()
                .providerId("priceProviderID-" + UUID.randomUUID())
                .build();
    }

    @Override
    public CheckoutInfo startCheckout(CheckoutRequest request) throws PaymentGatewayException {
        try {
            SessionCreateParams.Builder paramsBuilder = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.SUBSCRIPTION) // Assuming subscription for all plans
                    .setSuccessUrl(successUrl + "?subscription_id=" + request.subscriptionId().toString())
                    .setCancelUrl(cancelUrl + "?subscription_id=" + request.subscriptionId().toString())
                    .setSubscriptionData(SessionCreateParams.SubscriptionData.builder()
                            .putMetadata("subscription_id", request.subscriptionId().toString())
                            .putMetadata("user_id", request.userId().toString())
                            .build())
                    .putAllMetadata(Map.of(
                            "subscription_id", request.subscriptionId().toString(),
                            "user_id", request.userId().toString()))
                    .addLineItem(SessionCreateParams.LineItem.builder()
                            .setQuantity(1L)
                            .setPrice(request.priceProviderId())
                            .build());

            Session session = Session.create(paramsBuilder.build());

            return CheckoutInfo.builder()
                    .url(session.getUrl())
                    .subscriptionId(request.subscriptionId())
                    .build();
        } catch (StripeException e) {
            log.error("Stripe checkout session creation failed", e);
            throw new PaymentGatewayException("Failed to create checkout session: " + e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error during stripe checkout creation", e);
            throw new PaymentGatewayException("Unexpected error during checkout creation");
        }
    }
}
