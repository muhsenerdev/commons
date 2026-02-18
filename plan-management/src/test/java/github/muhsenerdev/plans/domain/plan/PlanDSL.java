package github.muhsenerdev.plans.domain.plan;

import java.util.Objects;
import java.util.UUID;

import github.muhsenerdev.commons.core.util.RandomUtil;

public class PlanDSL {

    private String code;
    private String description;
    private String title;
    private String name;
    private PlanType type;
    private int tier;

    public PlanDSL() {
        this.code = "TestPlan-" + UUID.randomUUID().toString();
        this.description = "Basic plan";
        this.title = "Basic Plan";
        this.name = "Basic Plan";
        this.type = PlanType.PAID;
        this.tier = RandomUtil.randomInt(1, 100000);
    }

    public static PlanDSL draftPlan() {
        return new PlanDSL();
    }

    public Plan build() {
        return Plan.builder()
                .code(code)
                .description(description)
                .title(title)
                .name(name)
                .type(type)
                .tier(tier)
                .build();
    }

    public Plan save(PlanRepository repository) {
        Plan plan = build();
        return repository.save(Objects.requireNonNull(plan));
    }

    public PlanDSL withTier(int tier) {
        this.tier = tier;
        return this;
    }

    public PlanDSL withCode(String code2) {
        this.code = code2;
        return this;
    }

    public PlanDSL withTitle(String title2) {
        this.title = title2;
        return this;
    }

    public PlanDSL withName(String name2) {
        this.name = name2;
        return this;
    }

    public PlanDSL withType(Object object) {
        this.type = (PlanType) object;
        return this;
    }
}
