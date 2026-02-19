package github.muhsenerdev.plans.domain.plan;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

import org.instancio.Instancio;
import org.springframework.test.util.ReflectionTestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import github.muhsenerdev.commons.core.exception.InvalidDomainException;
import github.muhsenerdev.commons.core.exception.InvalidInputException;
import github.muhsenerdev.commons.core.vo.Money;
import github.muhsenerdev.plans.domain.feature.Feature;
import github.muhsenerdev.plans.domain.feature.FeatureType;
import github.muhsenerdev.plans.domain.shared.Interval;

public class PlanTest {

    @Nested
    @DisplayName("Create Plan Tests")
    class CreatePlanTests {

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
    }

    @Nested
    @DisplayName("Activate Plan Tests")
    class ActivatePlanTests {

        @Test
        @DisplayName("Given a draft PAID plan with feature and price, when reserve for activation, then status becomes ACTIVATING and prices become ACTIVATING")
        void should_reserve_for_activation() {
            Plan plan = PlanDSL.aDraftPlan()
                    .withType(PlanType.PAID)
                    .withFeature(f -> f.withCode("AI_LIMIT").withValue("100").withType(FeatureType.QUOTA))
                    .withPrice(p -> p.withAmount(BigDecimal.TEN).withInterval(Interval.MONTHLY))
                    .build();

            plan.reserveForActivation();

            assertThat(plan.getStatus()).isEqualTo(PlanStatus.ACTIVATING);
            plan.getPrices().forEach(p -> assertThat(p.isActivating()).isTrue());
        }

        @Test
        @DisplayName("Given a FREE plan without features, when reserve for activation, then throws exception")
        void whenFreePlanHasNoFeatures_thenThrowsExceptionOnReservation() {
            Plan plan = PlanDSL.aDraftPlan()
                    .withType(PlanType.FREE)
                    .build();

            assertThatThrownBy(plan::reserveForActivation)
                    .isInstanceOf(PlanDomainException.class)
                    .hasFieldOrPropertyWithValue("code", "plan.atleast_one_feature");
        }

        @Test
        @DisplayName("Given a PAID plan without prices, when reserve for activation, then throws exception")
        void whenPaidPlanHasNoPrices_thenThrowsExceptionOnReservation() {
            Plan plan = PlanDSL.aDraftPlan()
                    .withType(PlanType.PAID)
                    .withFeature(f -> f.withValue("true"))
                    .build();

            assertThatThrownBy(plan::reserveForActivation)
                    .isInstanceOf(PlanDomainException.class)
                    .hasFieldOrPropertyWithValue("code", "plan.atleast_one_price");
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
                    .isInstanceOf(PlanDomainException.class)
                    .hasFieldOrPropertyWithValue("code", "plan.illegal_operation");
        }

        @Test
        @DisplayName("Given an ACTIVATING plan, when activation fails, then status becomes ACTIVATION_FAILED")
        void should_mark_as_failed_on_activation_failure() {
            // ARRANGE
            Plan plan = PlanDSL.aDraftPlan()
                    .withFeature()
                    .withPrice()
                    .build();
            plan.reserveForActivation();

            // ACT
            plan.activationFailed("Payment provider error");

            // ASSERT
            assertThat(plan.getStatus()).isEqualTo(PlanStatus.ACTIVATION_FAILED);
            assertThat(plan.getActivationFailReason()).isEqualTo("Payment provider error");
            plan.getPrices().forEach(p -> assertThat(p.isActivationFailed()).isTrue());
        }

    }

    @Nested
    @DisplayName("Update Plan Tests")
    class UpdatePlanTests {

        PlanInput input;

        @BeforeEach
        public void setup() {
            input = Instancio.of(PlanInput.class).create();
        }

        @Test
        @DisplayName("Given non-DRAFT plan, when updated, then throws Exception")
        void whenPlanIsUpdated_thenItIsDraft() {
            Plan plan = PlanDSL.aPlan()
                    .withStatus(PlanStatus.ACTIVE)
                    .build();

            assertThatThrownBy(() -> plan.updateFull(input))
                    .isInstanceOf(PlanDomainException.class)
                    .hasFieldOrPropertyWithValue("code", "plan.illegal_operation");
        }

    }

    @Nested
    @DisplayName("Delete Plan Tests")
    class DeletePlantests {

        @Test
        @DisplayName("Given non-DRAFT plan, when updated, then throws Exception")
        void givenActivePlan_whenDelete_thenThrowsException() {
            Plan plan = PlanDSL.aPlan()
                    .withStatus(PlanStatus.ACTIVE)
                    .build();

            assertThatThrownBy(plan::delete)
                    .isInstanceOf(PlanDomainException.class)
                    .hasFieldOrPropertyWithValue("code", "plan.illegal_operation");
        }

    }

    @Nested
    @DisplayName("Add Price Tests")
    class AddPriceTests {

        @ParameterizedTest
        @ValueSource(ints = { 0, 1 })
        @DisplayName("Given a draft plan, when add price is called, then adds a price in draft status")
        void givenDraftOrActivePlan_whenAddPrice_thenAddsPriceInDraftStatus(int value) {
            PlanStatus status = value == 0 ? PlanStatus.DRAFT : PlanStatus.ACTIVE;
            Plan plan = PlanDSL.aPlan()
                    .withType(PlanType.PAID)
                    .withStatus(status)
                    .build();

            plan.prices().add(Money.of(BigDecimal.TEN, "USD"), Interval.MONTHLY);

            assertThat(plan.getPrices().size()).isEqualTo(1);
            assertThat(plan.prices().getLast().getStatus()).isEqualTo(PriceStatus.DRAFT);
        }

        @Test
        @DisplayName("Given a DRAFT, FREE plan, when add price is called, then throws Exception")
        void givenFreePlan_whenAddPrice_thenThrowsException() {
            Plan plan = PlanDSL.aPlan()
                    .withType(PlanType.FREE)
                    .build();

            assertThatThrownBy(() -> plan.prices().add(Money.of(BigDecimal.TEN, "USD"), Interval.MONTHLY))
                    .isInstanceOf(PlanDomainException.class)
                    .hasFieldOrPropertyWithValue("code", "plan.illegal_operation");

        }

        @Test
        @DisplayName("Given a Plan with monthly draft price, when add price with the same interval, then throws Exception")
        void givenPlanWithMonthlyPrice_whenAddPriceWithSameInterval_thenThrowsException() {
            Plan plan = PlanDSL.aPlan()
                    .withType(PlanType.PAID)
                    .withPrice(p -> p.withInterval(Interval.MONTHLY))
                    .build();

            assertThatThrownBy(() -> plan.prices().add(Money.of(BigDecimal.TEN, "USD"), Interval.MONTHLY))
                    .isInstanceOf(PlanDomainException.class)
                    .hasFieldOrPropertyWithValue("code", "plan.duplicate_price");

        }

        @ParameterizedTest
        @DisplayName("Given a non-DRAFT or non-ACTIVE plan, when add price is called, then throws Exception")
        @ValueSource(ints = { 1, 2, 3 })
        void givenNonActiveOrNonDraftPlan_whenAddPrice_thenThrowsException(int caseNumber) {
            PlanStatus status = switch (caseNumber) {
                case 1 -> PlanStatus.ACTIVATING;
                case 2 -> PlanStatus.ACTIVATION_FAILED;
                case 3 -> PlanStatus.ARCHIVED;
                default -> throw new IllegalArgumentException("Invalid case number");
            };
            Plan plan = PlanDSL.aPlan()
                    .withType(PlanType.PAID)
                    .withStatus(status)
                    .build();

            assertThatThrownBy(() -> plan.prices().add(Money.of(BigDecimal.TEN, "USD"), Interval.MONTHLY))
                    .isInstanceOf(PlanDomainException.class)
                    .hasFieldOrPropertyWithValue("code", "plan.illegal_operation");

        }
    }

    @Nested
    @DisplayName("Delete Price tests")
    class DeletePriceTests {

        @Test
        @DisplayName("TEST 1: Given Plan with no prices, WHEN delete price, nothing happens")
        void givenPlanWithNoPrices_whenDeletePrice_nothingHappens() {
            Plan plan = PlanDSL.aPlan().build();
            UUID randomId = UUID.randomUUID();

            plan.prices().delete(randomId);

            assertThat(plan.getPrices()).isEmpty();
        }

        @ParameterizedTest
        @ValueSource(strings = { "DRAFT", "ACTIVE" })
        @DisplayName("TEST 2: Given Plan with DRAFT or ACTIVE status, WHEN delete price, price is removed from plan")
        void givenDraftOrActivePlan_whenDeletePrice_priceIsRemoved(String statusStr) {
            PlanStatus status = PlanStatus.valueOf(statusStr);
            Plan plan = PlanDSL.aPlan()
                    .withType(PlanType.PAID)
                    .withStatus(status)
                    .withPrice()
                    .build();
            UUID priceId = plan.prices().getLast().getId();

            plan.prices().delete(priceId);

            assertThat(plan.getPrices()).isEmpty();
        }

        @ParameterizedTest
        @ValueSource(strings = { "ACTIVATING", "ACTIVATION_FAILED", "ARCHIVED" })
        @DisplayName("TEST 3: Given Plan with nonDraft or nonActive status, When delete price, throws exception")
        void givenNonDraftOrNonActivePlan_whenDeletePrice_throwsException(String statusStr) {
            PlanStatus status = PlanStatus.valueOf(statusStr);
            Plan plan = PlanDSL.aPlan()
                    .withType(PlanType.PAID)
                    .withStatus(status)
                    .withPrice()
                    .build();
            UUID priceId = plan.prices().getLast().getId();

            assertThatThrownBy(() -> plan.prices().delete(priceId))
                    .isInstanceOf(PlanDomainException.class)
                    .hasFieldOrPropertyWithValue("code", "plan.illegal_operation");
        }

        @Test
        @DisplayName("TEST 4: Given a plan, Plan.prices().delete() method is called with null, then throws InvalidInputException")
        void givenPlan_whenDeletePriceWithNull_throwsException() {
            Plan plan = PlanDSL.aPlan().build();

            assertThatThrownBy(() -> plan.prices().delete(null))
                    .isInstanceOf(InvalidInputException.class);
        }

        @Test
        @DisplayName("TEST 5: Given a plan with non-draft price, when delete that price, then throws exception")
        void givenPlanWithNonDraftPrice_whenDeletePrice_throwsException() {
            Plan plan = PlanDSL.aPlan()
                    .withType(PlanType.PAID)
                    .withStatus(PlanStatus.DRAFT)
                    .withPrice(p -> p.withStatus(PriceStatus.ACTIVE))
                    .build();
            UUID priceId = plan.prices().getLast().getId();

            assertThatThrownBy(() -> plan.prices().delete(priceId))
                    .isInstanceOf(PlanDomainException.class)
                    .hasFieldOrPropertyWithValue("code", "plan.illegal_operation");
        }
    }

    @Nested
    @DisplayName("Add Feature tests")
    class AddFeatureTests {

        @ParameterizedTest
        @ValueSource(strings = { "DRAFT", "ACTIVE" })
        @DisplayName("TEST 1: Given Plan with DRAFT or ACTIVE status, WHEN add feature, it is allowed")
        void givenDraftOrActivePlan_whenAddFeature_isAllowed(String statusStr) {
            PlanStatus status = PlanStatus.valueOf(statusStr);
            Plan plan = PlanDSL.aPlan()
                    .withStatus(status)
                    .build();
            Feature feature = Feature.builder()
                    .code("FEAT-1")
                    .name("Test Feature")
                    .type(FeatureType.BOOLEAN)
                    .build();
            ReflectionTestUtils.setField(feature, "id", UUID.randomUUID());

            plan.features().add(feature, "true");

            assertThat(plan.getFeatures()).hasSize(1);
            assertThat(plan.features().getLast().getFeature().getCode()).isEqualTo("FEAT-1");
        }

        @ParameterizedTest
        @ValueSource(strings = { "ACTIVATING", "ACTIVATION_FAILED", "ARCHIVED" })
        @DisplayName("TEST 2: Given Plan with nonDraft and nonActive status, WHEN add feature, throws Exception")
        void givenNonDraftAndNonActivePlan_whenAddFeature_throwsException(String statusStr) {
            PlanStatus status = PlanStatus.valueOf(statusStr);
            Plan plan = PlanDSL.aPlan().withStatus(status).build();
            Feature feature = Feature.builder()
                    .code("FEAT-1")
                    .name("Test Feature")
                    .type(FeatureType.BOOLEAN)
                    .build();

            assertThatThrownBy(() -> plan.features().add(feature, "true"))
                    .isInstanceOf(PlanDomainException.class)
                    .hasFieldOrPropertyWithValue("code", "plan.illegal_operation");
        }

        @Test
        @DisplayName("TEST 3: Given a plan with a code, when add a feature with the same featureId, then throw")
        void givenPlanWithFeature_whenAddDuplicateFeature_throwsException() {
            Plan plan = PlanDSL.aDraftPlan()
                    .withFeature(f -> f.withCode("FEAT-DUP"))
                    .build();

            Feature existingFeature = plan.getFeatures().get(0).getFeature();

            assertThatThrownBy(() -> plan.features().add(existingFeature, "true"))
                    .isInstanceOf(PlanDomainException.class)
                    .hasFieldOrPropertyWithValue("code", "plan.feature.duplicate");
        }

        @Test
        @DisplayName("TEST 4: Happy path")
        void givenDraftPlan_whenAddFeature_successfullyAdded() {
            Plan plan = PlanDSL.aDraftPlan().build();
            Feature feature = Feature.builder()
                    .code("NEW-FEAT")
                    .name("New Feature")
                    .type(FeatureType.BOOLEAN)
                    .build();
            ReflectionTestUtils.setField(feature, "id", UUID.randomUUID());

            plan.features().add(feature, "true");

            assertThat(plan.getFeatures()).hasSize(1);
            assertThat(plan.features().getLast().getFeature().getCode()).isEqualTo("NEW-FEAT");
            assertThat(plan.features().getLast().getValue()).isEqualTo("true");
        }
    }

    @Nested
    @DisplayName("Delete Feature tests")
    class DeleteFeatureTests {

        @ParameterizedTest
        @ValueSource(strings = { "ACTIVATING", "ACTIVATION_FAILED", "ARCHIVED" })
        @DisplayName("TEST 1: Given Plan with nonDraft and nonActive status, WHEN delete feature, throws Exception")
        void givenNonDraftAndNonActivePlan_whenDeleteFeature_throwsException(String statusStr) {
            PlanStatus status = PlanStatus.valueOf(statusStr);
            Plan plan = PlanDSL.aPlan()
                    .withStatus(status)
                    .withFeature()
                    .build();
            UUID featureId = plan.getFeatures().get(0).getId();

            assertThatThrownBy(() -> plan.features().delete(featureId))
                    .isInstanceOf(PlanDomainException.class)
                    .hasFieldOrPropertyWithValue("code", "plan.illegal_operation");
        }

        @Test
        @DisplayName("TEST 2: Given Active Plan with only one feature, WHEN delete that feature, throws Exception")
        void givenActivePlanWithOneFeature_whenDeleteLastFeature_throwsException() {
            Plan plan = PlanDSL.aPlan()
                    .withStatus(PlanStatus.ACTIVE)
                    .withFeature()
                    .build();
            UUID featureId = plan.getFeatures().get(0).getId();

            assertThatThrownBy(() -> plan.features().delete(featureId))
                    .isInstanceOf(PlanDomainException.class)
                    .hasMessageContaining("Active plan must have at least one feature");
        }

        @Test
        @DisplayName("TEST 3: Given Plan with no such feature, WHEN delete feature, nothing happens")
        void givenPlan_whenDeleteNonExistentFeature_nothingHappens() {
            Plan plan = PlanDSL.aDraftPlan()
                    .withFeature()
                    .build();
            UUID randomId = UUID.randomUUID();

            plan.features().delete(randomId);

            assertThat(plan.getFeatures()).hasSize(1);
        }

        @Test
        @DisplayName("TEST 4: Happy path")
        void givenDraftPlanWithFeature_whenDeleteFeature_successfullyRemoved() {
            Plan plan = PlanDSL.aDraftPlan()
                    .withFeature()
                    .build();
            UUID featureId = plan.getFeatures().get(0).getId();

            plan.features().delete(featureId);

            assertThat(plan.getFeatures()).isEmpty();
        }
    }

    @Nested
    @DisplayName("Update Feature tests")
    class UpdateFeatureTests {

        @ParameterizedTest
        @ValueSource(strings = { "ACTIVATING", "ACTIVATION_FAILED", "ARCHIVED" })
        @DisplayName("TEST 1: Given Plan with nonDraft and nonActive status, WHEN update feature, throws Exception")
        void givenNonDraftAndNonActivePlan_whenUpdateFeature_throwsException(String statusStr) {
            PlanStatus status = PlanStatus.valueOf(statusStr);
            Plan plan = PlanDSL.aPlan()
                    .withStatus(status)
                    .withFeature()
                    .build();
            UUID featureId = plan.getFeatures().get(0).getId();

            assertThatThrownBy(() -> plan.features().updateValue(featureId, "new-value"))
                    .isInstanceOf(PlanDomainException.class)
                    .hasFieldOrPropertyWithValue("code", "plan.illegal_operation");
        }

        @Test
        @DisplayName("TEST 2: Given Plan with no such feature, WHEN update feature, nothing happens")
        void givenPlan_whenUpdateNonExistentFeature_nothingHappens() {
            Plan plan = PlanDSL.aDraftPlan()
                    .withFeature(f -> f.withValue("old-value"))
                    .build();
            UUID randomId = UUID.randomUUID();

            plan.features().updateValue(randomId, "new-value");

            assertThat(plan.getFeatures().get(0).getValue()).isEqualTo("old-value");
        }

        @Test
        @DisplayName("TEST 3: Happy path")
        void givenDraftPlanWithFeature_whenUpdateFeature_successfullyUpdated() {
            Plan plan = PlanDSL.aDraftPlan()
                    .withFeature(f -> f.withValue("old-value"))
                    .build();
            UUID featureId = plan.getFeatures().get(0).getId();

            plan.features().updateValue(featureId, "new-value");

            assertThat(plan.getFeatures().get(0).getValue()).isEqualTo("new-value");
        }
    }

    @Nested
    @DisplayName("Activate Price tests")
    class ActivatePriceTests {

        @ParameterizedTest
        @ValueSource(strings = { "DRAFT", "ACTIVATING", "ACTIVATION_FAILED", "ARCHIVED" })
        @DisplayName("TEST 1: Given Plan is not ACTIVE, WHEN reserve for activation, then throws Exception")
        void givenNonActivePlan_whenReservePriceForActivation_throwsException(String statusStr) {
            PlanStatus status = PlanStatus.valueOf(statusStr);
            Plan plan = PlanDSL.aPlan().withStatus(status).withPrice(p -> p.withStatus(PriceStatus.DRAFT)).build();
            UUID priceId = plan.getPrices().get(0).getId();

            assertThatThrownBy(() -> plan.prices().reserveForActivation(priceId, false))
                    .isInstanceOf(PlanDomainException.class)
                    .hasFieldOrPropertyWithValue("code", "plan.illegal_operation");
        }

        @Test
        @DisplayName("TEST 2: Given Active Plan, WHEN reserve non-existent price, then throws Exception")
        void givenActivePlan_whenReserveNonExistentPrice_throwsException() {
            Plan plan = PlanDSL.aPlan().withStatus(PlanStatus.ACTIVE).build();
            UUID randomId = UUID.randomUUID();

            assertThatThrownBy(() -> plan.prices().reserveForActivation(randomId, false))
                    .isInstanceOf(PlanDomainException.class)
                    .hasFieldOrPropertyWithValue("code", "plan.price_not_found");
        }

        @Test
        @DisplayName("TEST 3: Given Active Plan with an active price on same interval, WHEN reserve without override, then throws Exception")
        void givenPlanWithAnotherActivePriceSameInterval_whenReserveWithoutOverride_throwsException() {
            Plan plan = PlanDSL.aPlan()
                    .withStatus(PlanStatus.ACTIVE)
                    .withPrice(p -> p.withInterval(Interval.MONTHLY).withStatus(PriceStatus.ACTIVE))
                    .withPrice(p -> p.withInterval(Interval.MONTHLY).withStatus(PriceStatus.DRAFT))
                    .build();
            UUID draftPriceId = plan.getPrices().stream()
                    .filter(p -> p.getStatus() == PriceStatus.DRAFT)
                    .findFirst().get().getId();

            assertThatThrownBy(() -> plan.prices().reserveForActivation(draftPriceId, false))
                    .isInstanceOf(PlanDomainException.class)
                    .hasMessageContaining("An active price already exists for interval: MONTHLY");
        }

        @Test
        @DisplayName("TEST 4: Given Active Plan with an active price on same interval, WHEN reserve with override, then archives old price")
        void givenPlanWithAnotherActivePriceSameInterval_whenReserveWithOverride_archivesOldPrice() {
            Plan plan = PlanDSL.aPlan()
                    .withStatus(PlanStatus.ACTIVE)
                    .withPrice(p -> p.withInterval(Interval.MONTHLY).withStatus(PriceStatus.ACTIVE))
                    .withPrice(p -> p.withInterval(Interval.MONTHLY).withStatus(PriceStatus.DRAFT))
                    .build();

            PlanPrice activePrice = plan.getPrices().stream()
                    .filter(p -> p.getStatus() == PriceStatus.ACTIVE)
                    .findFirst().get();
            UUID draftPriceId = plan.getPrices().stream()
                    .filter(p -> p.getStatus() == PriceStatus.DRAFT)
                    .findFirst().get().getId();

            plan.prices().reserveForActivation(draftPriceId, true);

            assertThat(activePrice.getStatus()).isEqualTo(PriceStatus.ARCHIVING);
            assertThat(
                    plan.getPrices().stream().filter(p -> p.getId().equals(draftPriceId)).findFirst().get().getStatus())
                    .isEqualTo(PriceStatus.ACTIVATING);
        }

        @Test
        @DisplayName("TEST 5: Given Active Plan, WHEN reserve non-draft price, then throws Exception")
        void givenActivePlan_whenReserveNonDraftPrice_throwsException() {
            Plan plan = PlanDSL.aPlan()
                    .withStatus(PlanStatus.ACTIVE)
                    .withPrice(p -> p.withStatus(PriceStatus.ARCHIVED))
                    .build();
            UUID priceId = plan.getPrices().get(0).getId();

            assertThatThrownBy(() -> plan.prices().reserveForActivation(priceId, false))
                    .isInstanceOf(PlanDomainException.class)
                    .hasMessageContaining("Plan price is not in draft status");
        }

        @Test
        @DisplayName("TEST 6: Happy path: Beklenen price activating status'e çekilir")
        void givenActivePlan_whenReserveDraftPrice_statusBecomesActivating() {
            Plan plan = PlanDSL.aPlan()
                    .withStatus(PlanStatus.ACTIVE)
                    .withPrice(p -> p.withStatus(PriceStatus.DRAFT))
                    .build();
            UUID priceId = plan.getPrices().get(0).getId();

            plan.prices().reserveForActivation(priceId, false);

            assertThat(plan.getPrices().get(0).getStatus()).isEqualTo(PriceStatus.ACTIVATING);
        }
    }

    @Nested
    @DisplayName("Archive Price tests")
    class ArchivePriceTests {

        @ParameterizedTest
        @ValueSource(strings = { "DRAFT", "ACTIVATING", "ACTIVATION_FAILED", "ARCHIVED" })
        @DisplayName("TEST 1: Given Plan is not ACTIVE, WHEN reserve for archive, then throws Exception")
        void givenNonActivePlan_whenReservePriceForArchive_throwsException(String statusStr) {
            PlanStatus status = PlanStatus.valueOf(statusStr);
            Plan plan = PlanDSL.aPlan().withStatus(status).withPrice(p -> p.withStatus(PriceStatus.ACTIVE)).build();
            UUID priceId = plan.getPrices().get(0).getId();

            assertThatThrownBy(() -> plan.prices().reserveForArchive(priceId))
                    .isInstanceOf(PlanDomainException.class)
                    .hasFieldOrPropertyWithValue("code", "plan.illegal_operation");
        }

        @Test
        @DisplayName("TEST 2: Given Active Plan, WHEN reserve non-existent price for archive, then ignores")
        void givenActivePlan_whenReserveNonExistentPriceForArchive_ignores() {
            Plan plan = PlanDSL.aPlan().withStatus(PlanStatus.ACTIVE).withPrice().build();
            UUID randomId = UUID.randomUUID();

            plan.prices().reserveForArchive(randomId);

            assertThat(plan.getPrices().get(0).getStatus()).isEqualTo(PriceStatus.DRAFT);
        }

        @Test
        @DisplayName("TEST 3: Given Active Plan and non-active price, WHEN reserve for archive, then throws Exception")
        void givenActivePlan_whenReserveNonActivePriceForArchive_throwsException() {
            Plan plan = PlanDSL.aPlan()
                    .withStatus(PlanStatus.ACTIVE)
                    .withPrice(p -> p.withStatus(PriceStatus.DRAFT))
                    .withPrice(p -> p.withStatus(PriceStatus.ACTIVE))
                    .build();
            UUID priceId = plan.getPrices().get(0).getId();

            assertThatThrownBy(() -> plan.prices().reserveForArchive(priceId))
                    .isInstanceOf(PlanDomainException.class)
                    .hasMessageContaining("To archive a price, it must be in active status");
        }

        @Test
        @DisplayName("TEST 4: Happy path: Active price becomes ARCHIVING")
        void givenActivePlanAndActivePrice_whenReserveForArchive_statusBecomesArchiving() {
            Plan plan = PlanDSL.aPlan()
                    .withStatus(PlanStatus.ACTIVE)
                    .withPrice(p -> p.withStatus(PriceStatus.ACTIVE))
                    .withPrice(p -> p.withStatus(PriceStatus.ACTIVE))
                    .build();
            UUID priceId = plan.getPrices().get(0).getId();

            plan.prices().reserveForArchive(priceId);

            assertThat(plan.getPrices().get(0).getStatus()).isEqualTo(PriceStatus.ARCHIVING);
        }

        @Test
        @DisplayName("TEST 5: Given Active Plan with only one price, WHEN reserve for archive, then throws Exception")
        void givenActivePlanWithOnlyOnePrice_whenReserveForArchive_throwsException() {
            Plan plan = PlanDSL.aPlan()
                    .withStatus(PlanStatus.ACTIVE)
                    .withPrice(p -> p.withStatus(PriceStatus.ACTIVE))
                    .build();
            UUID priceId = plan.getPrices().get(0).getId();

            assertThatThrownBy(() -> plan.prices().reserveForArchive(priceId))
                    .isInstanceOf(PlanDomainException.class)
                    .hasMessageContaining("Active plan must have at least one active price.");
        }
    }
}
