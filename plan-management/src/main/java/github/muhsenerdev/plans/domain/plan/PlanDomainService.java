package github.muhsenerdev.plans.domain.plan;

import java.util.Objects;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PlanDomainService {

    private final PlanRepository planRepository;

    public Plan createPlan(PlanInput input) {
        ensureCodeIsUnique(input.code());
        ensureTierIsUnique(input.tier());
        return Plan.builder()
                .code(input.code())
                .description(input.description())
                .title(input.title())
                .name(input.name())
                .type(input.type())
                .tier(input.tier())
                .build();
    }

    private void ensureCodeIsUnique(String code) {
        if (planRepository.existsByCode(code)) {
            throw PlanDomainException.codeMustBeUnique(code);
        }
    }

    private void ensureTierIsUnique(int tier) {
        if (planRepository.existsByTier(tier)) {
            throw PlanDomainException.tierMustBeUnique(tier);
        }
    }

    // public void updatePlan(Plan plan, PlanBasicsInput input) {
    // if (!Objects.equals(plan.getTier(), input.tier())) {
    // ensureTierIsUnique(input.tier());
    // }
    // plan.updateBasics(input.name(), input.title(), input.description(),
    // input.tier());
    // }

    public void updatePlan(Plan plan, PlanInput input) {
        // If tier or code is changed, ensure they are unique
        if (!Objects.equals(plan.getTier(), input.tier())) {
            ensureTierIsUnique(input.tier());
        }

        if (!Objects.equals(plan.getCode(), input.code())) {
            ensureCodeIsUnique(input.code());
        }
        plan.updateFull(input);
    }

}
