package github.muhsenerdev.plans.infra.webhook;

import java.time.Instant;
import java.util.UUID;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
// import com.github.muhsenerdev.langpra.plans.application.checkout.CyclePaymentFailedHandler;
// import com.github.muhsenerdev.langpra.plans.application.checkout.SubscriptionCycledHandler;
// import com.github.muhsenerdev.langpra.plans.application.checkout.SubscriptionDeletedHandler;
// import com.github.muhsenerdev.langpra.plans.application.checkout.SubscriptionStartedHandler;
// import com.github.muhsenerdev.langpra.plans.infra.config.StripeConfig;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.net.Webhook;

import github.muhsenerdev.plans.application.subscription.checkout.CyclePaymentFailedHandler;
import github.muhsenerdev.plans.application.subscription.checkout.SubscriptionCycledHandler;
import github.muhsenerdev.plans.application.subscription.checkout.SubscriptionDeletedHandler;
import github.muhsenerdev.plans.application.subscription.checkout.SubscriptionStartedHandler;
import github.muhsenerdev.plans.domain.subscription.SubscriptionInfo;
import github.muhsenerdev.plans.infra.config.StripeConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequestMapping("/webhook")
@RestController
@RequiredArgsConstructor
@Slf4j
public class WebhookListenerController {

    private final StripeEventService stripeEventService;
    private final StripeConfig stripeConfig;
    private final ObjectMapper objectMapper;
    private final SubscriptionStartedHandler subscriptionStartedHandler;
    private final SubscriptionCycledHandler subscriptionCycledHandler;
    private final CyclePaymentFailedHandler cyclePaymentFailedHandler;
    private final SubscriptionDeletedHandler subscriptionDeletedHandler;

    @PostMapping("/stripe")
    public String handleStripeWebhook(@RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader) throws JsonMappingException, JsonProcessingException {

        log.info("Received Stripe webhook. Payload: {}", payload);

        Event event;
        try {
            event = Webhook.constructEvent(payload, sigHeader, stripeConfig.getWebhookSecret());
        } catch (SignatureVerificationException e) {
            log.warn("Stripe signature verification failed.", e);
            return "OK";
        }

        String eventId = event.getId();

        if (!stripeEventService.tryCreate(eventId)) {
            return "ALREADY_PROCESSED";
        }

        try {
            processEvent(event);
            stripeEventService.markAsDone(eventId);
            return "OK";
        } catch (Exception e) {
            log.error("Error processing Stripe event: {}", eventId, e);
            stripeEventService.markAsFailed(eventId, e.getMessage());
            throw e;
        }
    }

    private void processEvent(Event event) throws JsonMappingException, JsonProcessingException {
        String eventType = event.getType();

        if ("invoice.paid".equals(eventType)) {
            // Using Jackson because Event.getDataObjectDeserializer() might be tricky with
            // new API versions sometimes,
            // but let's try to be consistent with previous implementation or use Stripe
            // models.
            // The previous implementation used manual JSON navigation. Let's try to map it
            // to Invoice model.

            // Re-navigating for metadata because the structure in the request was specific
            var tree = objectMapper.readTree(event.toJson());
            var invoiceNode = tree.get("data").get("object");
            var billingReason = invoiceNode.get("billing_reason").asText();
            var subscriptionDetails = invoiceNode.get("parent").get("subscription_details");

            String subscriptionIdString = subscriptionDetails.get("metadata")
                    .get("subscription_id").asText();
            UUID subscriptionId = UUID.fromString(subscriptionIdString);
            String providerSubscriptionId = subscriptionDetails.get("subscription").asText();

            var lineItemPeriod = invoiceNode.get("lines").get("data").get(0).get("period");
            var periodStart = lineItemPeriod.get("start").asLong();
            var periodEnd = lineItemPeriod.get("end").asLong();

            SubscriptionInfo details = SubscriptionInfo.builder()
                    .subscriptionId(subscriptionId)
                    .providerId(providerSubscriptionId)
                    .periodStart(Instant.ofEpochSecond(periodStart))
                    .periodEnd(Instant.ofEpochSecond(periodEnd))
                    .build();

            if ("subscription_create".equals(billingReason)) {
                subscriptionStartedHandler.handle(details);
                log.info("Subscription started. Handling... {}", details);
            } else if ("subscription_cycle".equals(billingReason)) {
                log.info("Subscription cycled. Handling... {}", details);
                subscriptionCycledHandler.handle(details);
            }
        } else if ("invoice.payment_failed".equals(eventType)) {
            var tree = objectMapper.readTree(event.toJson());
            var invoiceNode = tree.get("data").get("object");
            var billingReason = invoiceNode.get("billing_reason").asText();

            if ("subscription_cycle".equals(billingReason)) {
                var subscriptionDetails = invoiceNode.get("parent").get("subscription_details");
                String subscriptionIdString = subscriptionDetails.get("metadata")
                        .get("subscription_id").asText();
                UUID subscriptionId = UUID.fromString(subscriptionIdString);

                log.info("Subscription payment failed for cycle. Handling... subscriptionId: {}", subscriptionId);
                cyclePaymentFailedHandler.handle(subscriptionId);
            }
        } else if ("customer.subscription.deleted".equals(eventType)) {
            var tree = objectMapper.readTree(event.toJson());
            var subNode = tree.get("data").get("object");

            String subscriptionIdString = subNode.get("metadata").get("subscription_id").asText();
            UUID subscriptionId = UUID.fromString(subscriptionIdString);

            String reason = "unknown";
            if (subNode.has("cancellation_details") && subNode.get("cancellation_details").has("reason")) {
                reason = subNode.get("cancellation_details").get("reason").asText();
            }

            SubscriptionInfo details = SubscriptionInfo.builder()
                    .subscriptionId(subscriptionId)
                    .cancellationReason(reason)
                    .build();

            log.info("Subscription deleted. Handling... subscriptionId: {}, reason: {}", subscriptionId, reason);
            subscriptionDeletedHandler.handle(details);
        }
    }
}
