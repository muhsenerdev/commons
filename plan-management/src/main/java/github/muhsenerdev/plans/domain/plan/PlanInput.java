package github.muhsenerdev.plans.domain.plan;

import lombok.Builder;

@Builder(toBuilder = true)
public record PlanInput(
        String code,
        String description,
        String title,
        String name,
        PlanType type,
        int tier) {

}
