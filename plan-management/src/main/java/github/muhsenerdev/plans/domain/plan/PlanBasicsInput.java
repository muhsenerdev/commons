package github.muhsenerdev.plans.domain.plan;

public record PlanBasicsInput(
        String description,
        String title,
        String name,
        int tier) {

}
