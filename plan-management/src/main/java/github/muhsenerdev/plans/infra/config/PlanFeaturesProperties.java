package github.muhsenerdev.plans.infra.config;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import github.muhsenerdev.plans.domain.feature.FeatureType;
import lombok.Getter;
import lombok.Setter;

@Configuration
@ConfigurationProperties(prefix = "plans")
@Getter
@Setter
public class PlanFeaturesProperties {

    private List<CoreFeature> coreFeatures;

    @Getter
    @Setter
    public static class CoreFeature {
        private String code;
        private String name;
        private FeatureType type;
    }
}
