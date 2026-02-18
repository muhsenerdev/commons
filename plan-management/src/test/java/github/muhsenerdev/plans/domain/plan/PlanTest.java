package github.muhsenerdev.plans.domain.plan;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import github.muhsenerdev.commons.core.exception.InvalidDomainException;
import github.muhsenerdev.plans.domain.feature.FeatureType;
import github.muhsenerdev.plans.domain.shared.Interval;

public class PlanTest {

    @Test
    @DisplayName("When plan is created, then it is in draft state")
    void testCreatePlan() {
        Plan plan = PlanDSL.aDraftPlan().build();

        assertThat(plan.isDraft()).isTrue();
    }

    @ParameterizedTest
    @ValueSource(ints = { -1, 0 })
    @DisplayName("When tier is not positive, then throws exception")
    void whenTierIsNotPositive_thenThrowsException(int tier) {
        assertThatThrownBy(() -> PlanDSL.aDraftPlan().withTier(tier).build())
                .isInstanceOf(InvalidDomainException.class);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = { " ", "\t" })
    @DisplayName("When code is null or blank, then throws Exception")
    void whenCodeIsNullOrEmpty_thenThrowsException(String code) {
        assertThatThrownBy(() -> PlanDSL.aDraftPlan().withCode(code).build())
                .isInstanceOf(InvalidDomainException.class);
    }

    @Test
    @DisplayName("When type is null, then throws Exception")
    void whenTypeIsNull_thenThrowsException() {
        assertThatThrownBy(() -> PlanDSL.aDraftPlan().withType(null).build())
                .isInstanceOf(InvalidDomainException.class);
    }

    @Test
    @DisplayName("Given a draft PAID plan with feature and price, when reserve for activation, then status becomes ACTIVATING")
    void should_reserve_for_activation() {
        Plan plan = PlanDSL.aDraftPlan()
                .withType(PlanType.PAID)
                .withFeature(f -> f.withCode("AI_LIMIT").withValue("100").withType(FeatureType.QUOTA))
                .withPrice(p -> p.withAmount(BigDecimal.TEN).withInterval(Interval.MONTHLY))
                .build();

        plan.reserveForActivation();

        assertThat(plan.getStatus()).isEqualTo(PlanStatus.ACTIVATING);
    }

    @Test
    @DisplayName("Given a FREE plan without features, when reserve for activation, then throws exception")
    void whenFreePlanHasNoFeatures_thenThrowsExceptionOnReservation() {
        Plan plan = PlanDSL.aDraftPlan()
                .withType(PlanType.FREE)
                .build();

        assertThatThrownBy(plan::reserveForActivation)
                .isInstanceOf(InvalidDomainException.class)
                .hasMessageContaining("Plan must have at least one feature");
    }

    @Test
    @DisplayName("Given a PAID plan without prices, when reserve for activation, then throws exception")
    void whenPaidPlanHasNoPrices_thenThrowsExceptionOnReservation() {
        Plan plan = PlanDSL.aDraftPlan()
                .withType(PlanType.PAID)
                .withFeature(f -> f.withValue("true"))
                .build();

        assertThatThrownBy(plan::reserveForActivation)
                .isInstanceOf(InvalidDomainException.class)
                .hasMessageContaining("PAID plans must have at least one price");
    }

    @Test
    @DisplayName("Given an ACTIVATING plan, when activate is called with provider IDs, then plan and prices become ACTIVE")
    void should_activate_plan() {
        Plan plan = PlanDSL.aDraftPlan()
                .withType(PlanType.PAID)
                .withFeature(f -> f.withCode("FEAT1").withValue("100"))
                .withPrice(p -> p.withAmount(BigDecimal.TEN).withInterval(Interval.MONTHLY))
                .build();

        plan.reserveForActivation();

        UUID priceId = plan.prices().getLast().getId();
        plan.activate("prod_123", Map.of(priceId, "price_123"));

        assertThat(plan.getStatus()).isEqualTo(PlanStatus.ACTIVE);
        assertThat(plan.getStripeProductId()).isEqualTo("prod_123");

        PlanPrice price = plan.prices().getLast();
        assertThat(price.getStripePriceId()).isEqualTo("price_123");
        assertThat(price.isActive()).isTrue();
    }

    @Test
    @DisplayName("Given a DRAFT plan, when activate is called, then throws exception")
    void whenPlanIsNotActivating_thenThrowsExceptionOnActivate() {
        Plan plan = PlanDSL.aDraftPlan().build();

        assertThatThrownBy(() -> plan.activate("prod_123", Map.of()))
                .isInstanceOf(InvalidDomainException.class)
                .hasMessageContaining("Plan must be in ACTIVATING status");
    }

    @Test
    @DisplayName("Given an ACTIVATING plan, when activation fails, then status becomes ACTIVATION_FAILED")
    void should_mark_as_failed_on_activation_failure() {
        Plan plan = PlanDSL.aDraftPlan()
                .withFeature(f -> f.withValue("10"))
                .build();
        plan.reserveForActivation();

        plan.activationFailed("Payment provider error");

        assertThat(plan.getStatus()).isEqualTo(PlanStatus.ACTIVATION_FAILED);
        assertThat(plan.getActivationFailReason()).isEqualTo("Payment provider error");
    }
}
