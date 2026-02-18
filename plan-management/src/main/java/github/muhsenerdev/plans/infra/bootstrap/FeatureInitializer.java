package github.muhsenerdev.plans.infra.bootstrap;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import github.muhsenerdev.plans.domain.feature.Feature;
import github.muhsenerdev.plans.domain.feature.FeatureRepository;
import github.muhsenerdev.plans.infra.config.PlanFeaturesProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class FeatureInitializer implements CommandLineRunner {

    private final FeatureRepository featureRepository;
    private final PlanFeaturesProperties properties;

    @Override
    @Transactional
    public void run(String... args) {
        log.info("Initializing core features from configuration...");

        if (properties.getCoreFeatures() == null || properties.getCoreFeatures().isEmpty()) {
            log.warn("No core features found in configuration!");
            return;
        }

        properties.getCoreFeatures().forEach(coreFeature -> {
            boolean exists = featureRepository.existsByCode(coreFeature.getCode());
            if (!exists) {
                Feature feature = Feature.builder()
                        .code(coreFeature.getCode())
                        .name(coreFeature.getName())
                        .type(coreFeature.getType())
                        .build();
                featureRepository.save(feature);
                log.info("Core feature created: {}", coreFeature.getCode());
            } else {
                log.debug("Core feature already exists: {}", coreFeature.getCode());
            }
        });

        log.info("Core feature initialization completed.");
    }
}
