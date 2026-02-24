package github.muhsenerdev.plans.domain.plan;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import org.springframework.test.util.ReflectionTestUtils;

import github.muhsenerdev.commons.core.util.RandomUtil;
import github.muhsenerdev.commons.core.vo.Money;
import github.muhsenerdev.plans.domain.feature.Feature;
import github.muhsenerdev.plans.domain.feature.FeatureRepository;
import github.muhsenerdev.plans.domain.feature.FeatureType;
import github.muhsenerdev.plans.domain.shared.Interval;

public class PlanDSL {

    private String code;
    private String description;
    private String title;
    private String name;
    private PlanType type;
    private int tier;
    private final List<FeatureConfig> features = new ArrayList<>();
    private final List<PriceConfig> prices = new ArrayList<>();
    private PlanStatus status;

    public PlanDSL() {
        this.code = "PLAN-" + UUID.randomUUID().toString().substring(0, 8);
        this.description = "Test description";
        this.title = "Test Title";
        this.name = "Test Name";
        this.type = PlanType.FREE;
        this.tier = RandomUtil.randomInt(1, 1000000);
    }

    public static PlanDSL aDraftPlan() {
        return new PlanDSL();
    }

    public static PlanDSL anActivePlan() {
        return new PlanDSL();
    }

    public static PlanDSL aPlan() {
        return new PlanDSL();
    }

    public PlanDSL withType(PlanType type) {
        this.type = type;
        return this;
    }

    public PlanDSL withTier(int tier) {
        this.tier = tier;
        return this;
    }

    public PlanDSL withCode(String code) {
        this.code = code;
        return this;
    }

    public PlanDSL withTitle(String title) {
        this.title = title;
        return this;
    }

    public PlanDSL withName(String name) {
        this.name = name;
        return this;
    }

    public PlanDSL withStatus(PlanStatus status) {
        this.status = status;
        return this;
    }

    public PlanDSL withFeature(Consumer<FeatureBuilder> featureBuilderConsumer) {
        FeatureBuilder builder = new FeatureBuilder();
        featureBuilderConsumer.accept(builder);
        this.features.add(builder.buildConfig());
        return this;
    }

    public PlanDSL withFeature() {
        FeatureBuilder builder = new FeatureBuilder();
        this.features.add(builder.buildConfig());
        return this;
    }

    public PlanDSL withPrice(Consumer<PriceBuilder> priceBuilderConsumer) {
        PriceBuilder builder = new PriceBuilder();
        priceBuilderConsumer.accept(builder);
        this.prices.add(builder.buildConfig());
        return this;
    }

    public PlanDSL withPrice() {
        PriceBuilder builder = new PriceBuilder();
        this.prices.add(builder.buildConfig());
        return this;
    }

    public Plan build() {
        Plan plan = Plan.builder()
                .code(code)
                .description(description)
                .title(title)
                .name(name)
                .type(type)
                .tier(tier)
                .build();
        if (status != null) {
            ReflectionTestUtils.setField(plan, "status", status);
        }

        List<PlanFeature> planFeatures = new ArrayList<>();
        for (FeatureConfig config : features) {
            Feature feature = Feature.builder()
                    .code(config.code)
                    .name(config.name)
                    .type(config.type)
                    .build();
            ReflectionTestUtils.setField(feature, "id", config.id);
            PlanFeature planFeature = PlanFeature.create(plan, feature, config.value);
            ReflectionTestUtils.setField(planFeature, "id", config.id);
            planFeatures.add(planFeature);
        }
        ReflectionTestUtils.setField(plan, "features", planFeatures);

        List<PlanPrice> planPrices = new ArrayList<>();
        for (PriceConfig config : prices) {
            PlanPrice planPrice = PlanPrice.builder()
                    .plan(plan)
                    .price(Money.of(config.amount, config.currency))
                    .interval(config.interval)
                    .build();
            ReflectionTestUtils.setField(planPrice, "id", UUID.randomUUID());
            ReflectionTestUtils.setField(planPrice, "status", config.status);
            planPrices.add(planPrice);
        }
        ReflectionTestUtils.setField(plan, "prices", planPrices);

        return plan;
    }

    public Plan save(PlanRepository repository) {
        return repository.save(build());
    }

    public Plan save(PlanRepository planRepository, FeatureRepository featureRepository) {
        Plan plan = Plan.builder()
                .code(code)
                .description(description)
                .title(title)
                .name(name)
                .type(type)
                .tier(tier)
                .build();

        for (FeatureConfig config : features) {
            Feature feature = featureRepository.findByCode(config.code)
                    .orElseGet(() -> featureRepository.save(Feature.builder()
                            .code(config.code)
                            .name(config.name)
                            .type(config.type)
                            .build()));
            plan.features().add(feature, config.value);
        }

        for (PriceConfig config : prices) {
            Money money = Money.of(config.amount, config.currency);
            plan.prices().add(money, config.interval);
        }

        return planRepository.save(plan);
    }

    public static class FeatureBuilder {
        private UUID id = UUID.randomUUID();
        private String code = "FEAT-" + UUID.randomUUID().toString().substring(0, 8);
        private String name = "Feature Name";
        private FeatureType type = FeatureType.BOOLEAN;
        private String value = "true";

        public FeatureBuilder withId(UUID id) {
            this.id = id;
            return this;
        }

        public FeatureBuilder withCode(String code) {
            this.code = code;
            return this;
        }

        public FeatureBuilder withName(String name) {
            this.name = name;
            return this;
        }

        public FeatureBuilder withType(FeatureType type) {
            this.type = type;
            return this;
        }

        public FeatureBuilder withValue(String value) {
            this.value = value;
            return this;
        }

        FeatureConfig buildConfig() {
            return new FeatureConfig(id, code, name, type, value);
        }
    }

    public static class PriceBuilder {
        private BigDecimal amount = BigDecimal.TEN;
        private String currency = "USD";
        private Interval interval = Interval.MONTHLY;
        private PriceStatus status = PriceStatus.DRAFT;

        public PriceBuilder withAmount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public PriceBuilder withAmount(int amount) {
            this.amount = BigDecimal.valueOf(amount);
            return this;
        }

        public PriceBuilder withCurrency(String currency) {
            this.currency = currency;
            return this;
        }

        public PriceBuilder withInterval(Interval interval) {
            this.interval = interval;
            return this;
        }

        public PriceBuilder withStatus(PriceStatus status) {
            this.status = status;
            return this;
        }

        PriceConfig buildConfig() {
            return new PriceConfig(amount, currency, interval, status);
        }
    }

    private record FeatureConfig(UUID id, String code, String name, FeatureType type, String value) {
    }

    private record PriceConfig(BigDecimal amount, String currency, Interval interval, PriceStatus status) {
    }
}
