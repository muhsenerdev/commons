package github.muhsenerdev.plans.application.feature.shared;

import java.util.UUID;
import github.muhsenerdev.plans.domain.feature.FeatureType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder(toBuilder = true)
@Schema(description = "Response for a feature")
public record FeatureResponse(
        UUID id,
        String code,
        String name,
        FeatureType type) {
}
