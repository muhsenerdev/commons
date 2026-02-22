package github.muhsenerdev.plans.infra.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import com.stripe.Stripe;

import jakarta.annotation.PostConstruct;

@Configuration
public class StripeConfig {

    @Value("${app.stripe.api-key}")
    private String apiKey;

    @Value("${app.stripe.webhook-secret}")
    private String webhookSecret;

    public String getWebhookSecret() {
        return webhookSecret;
    }

    @PostConstruct
    public void init() {
        Stripe.apiKey = apiKey;
    }
}
