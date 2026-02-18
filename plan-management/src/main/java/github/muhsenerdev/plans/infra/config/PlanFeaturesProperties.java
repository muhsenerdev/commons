package github.muhsenerdev.plans.infra.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import github.muhsenerdev.plans.domain.feature.FeatureType;
import lombok.Data;

@Component
@ConfigurationProperties(prefix = "plans")
@Data
public class PlanFeaturesProperties {

    private List<CoreFeature> coreFeatures = new ArrayList<>();

    @Data
    public static class CoreFeature {
        private String code;
        private String name;
        private FeatureType type;
    }
}
