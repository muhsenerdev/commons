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
public class PlanModuleProperties {

    private List<CoreFeature> coreFeatures = new ArrayList<>();
    private DefaultPlanConfig defaultPlan = new DefaultPlanConfig();

    @Data
    public static class CoreFeature {
        private String code;
        private String name;
        private FeatureType type;
    }

    @Data
    public static class DefaultPlanConfig {
        private boolean enabled = false;
        private String code;
        private String name;
        private int tier;
        private String title;
        private String description;
        private List<PlanFeatureItem> features = new ArrayList<>();

    }

    @Data
    public static class PlanFeatureItem {
        private String code;
        private String value;
    }
}
