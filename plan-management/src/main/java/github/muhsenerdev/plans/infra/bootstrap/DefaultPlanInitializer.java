package github.muhsenerdev.plans.infra.bootstrap;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import github.muhsenerdev.plans.domain.feature.Feature;
import github.muhsenerdev.plans.domain.feature.FeatureRepository;
import github.muhsenerdev.plans.domain.plan.Plan;
import github.muhsenerdev.plans.domain.plan.PlanRepository;
import github.muhsenerdev.plans.domain.plan.PlanType;
import github.muhsenerdev.plans.infra.config.PlanModuleProperties;
import github.muhsenerdev.plans.infra.config.PlanModuleProperties.DefaultPlanConfig;
import github.muhsenerdev.plans.infra.config.PlanModuleProperties.PlanFeatureItem;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Configuration
@RequiredArgsConstructor
@Slf4j
@DependsOn("featureInitializer")
public class DefaultPlanInitializer {

    private final PlanModuleProperties properties;
    private final PlanRepository planRepository;
    private final FeatureRepository featureRepository;

    @Transactional
    @PostConstruct
    public void initializeDefaultPlan() {
        DefaultPlanConfig config = properties.getDefaultPlan();

        if (config == null || !config.isEnabled()) {
            log.info("Default plan initialization is disabled. Skipping...");
            return;
        }

        log.info("Default plan feature is enabled. Checking configuration...");

        // Validate properties
        if (config.getCode() == null || config.getCode().isBlank()) {
            log.error("Default plan code is missing in configuration!");
            throw new IllegalStateException("Default plan code must be provided when default plan is enabled.");
        }
        if (config.getName() == null || config.getName().isBlank()) {
            log.error("Default plan name is missing in configuration!");
            throw new IllegalStateException("Default plan name must be provided when default plan is enabled.");
        }
        if (config.getTitle() == null || config.getTitle().isBlank()) {
            log.error("Default plan title is missing in configuration!");
            throw new IllegalStateException("Default plan title must be provided when default plan is enabled.");
        }
        if (config.getTier() <= 0) {
            log.error("Default plan tier is invalid in configuration: {}", config.getTier());
            throw new IllegalStateException("Default plan tier must be greater than 0 when default plan is enabled.");
        }
        if (config.getFeatures() == null || config.getFeatures().isEmpty()) {
            log.error("Default plan features are missing in configuration!");
            throw new IllegalStateException(
                    "Default plan must have at least one feature when default plan is enabled.");
        }

        // Check if plan exists
        if (planRepository.existsByCode(config.getCode())) {
            log.info("Default plan with code '{}' already exists. Skipping initialization.", config.getCode());
            return;
        }

        log.info("Creating default FREE plan from configuration: code={}", config.getCode());

        try {
            Plan plan = Plan.builder()
                    .code(config.getCode())
                    .name(config.getName())
                    .title(config.getTitle())
                    .description(config.getDescription())
                    .type(PlanType.FREE)
                    .tier(config.getTier())
                    .build();

            // Add features
            for (PlanFeatureItem item : config.getFeatures()) {
                if (item.getCode() == null || item.getCode().isBlank()) {
                    throw new IllegalStateException("Feature code cannot be blank in default plan configuration.");
                }
                Feature feature = featureRepository.findByCode(item.getCode())
                        .orElseThrow(() -> new IllegalStateException(
                                "Feature with code '" + item.getCode()
                                        + "' not found in the system! Cannot initialize default plan."));

                plan.features().add(feature, item.getValue());
                log.debug("Added feature '{}' to default plan with value: {}", feature.getCode(), item.getValue());
            }

            // Reserve and activate plan
            plan.reserveForActivation();
            plan.activate(null, null);

            planRepository.save(plan);
            log.info("Default FREE plan '{}' initialized and activated successfully.", config.getCode());

        } catch (IllegalStateException e) {
            log.error("Failed to initialize default FREE plan due to configuration error: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Failed to initialize default FREE plan: {}", e.getMessage(), e);
            throw new IllegalStateException("Failed to initialize default FREE plan.", e);
        }
    }
}
