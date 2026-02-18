package github.muhsenerdev.plans.domain.plan;

public record PlanFullInput(
        String name,
        String title,
        String description,
        int tier,
        PlanType planType,
        String code) {

}
