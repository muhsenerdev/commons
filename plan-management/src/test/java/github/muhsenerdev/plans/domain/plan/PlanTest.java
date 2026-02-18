
package github.muhsenerdev.plans.domain.plan;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import github.muhsenerdev.commons.core.exception.InvalidDomainException;

public class PlanTest {

    @Test
    @DisplayName("When plan is created, then it is in draft state")
    void testCreatePlan() {
        Plan plan = PlanDSL.draftPlan().build();

        assertThat(plan.isDraft()).isTrue();
    }

    @ParameterizedTest
    @ValueSource(ints = { -1, 0 })
    @DisplayName("When tier is not positive, then throws exception")
    void whenTierIsNotPositive_thenThrowsException(int tier) {

        assertThatThrownBy(() -> PlanDSL.draftPlan().withTier(tier).build())
                .isInstanceOf(InvalidDomainException.class);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = { " ", "\t" })
    @DisplayName("When code is null or blank, then throws Exception")
    void whenCodeIsNullOrEmpty_thenThrowsException(String code) {
        assertThatThrownBy(() -> PlanDSL.draftPlan().withCode(code).build())
                .isInstanceOf(InvalidDomainException.class);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = { " ", "\t" })
    @DisplayName("When title is null or blank, then throws Exception")
    void whenTitleIsNullOrEmpty_thenThrowsException(String title) {
        assertThatThrownBy(() -> PlanDSL.draftPlan().withTitle(title).build())
                .isInstanceOf(InvalidDomainException.class);
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = { " ", "\t" })
    @DisplayName("When name is null or blank, then throws Exception")
    void whenNameIsNullOrEmpty_thenThrowsException(String name) {
        assertThatThrownBy(() -> PlanDSL.draftPlan().withName(name).build())
                .isInstanceOf(InvalidDomainException.class);
    }

    @Test
    @DisplayName("When type is null, then throws Exception")
    void whenTypeIsNull_thenThrowsException() {
        assertThatThrownBy(() -> PlanDSL.draftPlan().withType(null).build())
                .isInstanceOf(InvalidDomainException.class);
    }

}
