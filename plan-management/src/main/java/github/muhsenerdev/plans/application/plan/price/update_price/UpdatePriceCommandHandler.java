// package github.muhsenerdev.plans.application.plan.price.update_price;

// import org.springframework.stereotype.Component;
// import org.springframework.transaction.annotation.Transactional;

// import github.muhsenerdev.plans.application.plan.shared.PlanMapper;
// import github.muhsenerdev.plans.application.plan.shared.PlanService;
// import github.muhsenerdev.plans.domain.plan.Plan;
// import github.muhsenerdev.plans.domain.plan.PlanRepository;
// import lombok.RequiredArgsConstructor;

// @Component
// @RequiredArgsConstructor
// public class UpdatePriceCommandHandler {

// private final PlanService planService;
// private final PlanRepository planRepository;
// private final PlanMapper planMapper;

// @Transactional
// public void handle(UpdatePriceCommand command) {
// Plan plan = planService.findWithPricesOrThrow(command.getPlanId());

// plan.prices().update(
// command.getPriceId(),
// planMapper.extractMoney(command),
// command.getInterval());

// planRepository.save(plan);
// }
// }
