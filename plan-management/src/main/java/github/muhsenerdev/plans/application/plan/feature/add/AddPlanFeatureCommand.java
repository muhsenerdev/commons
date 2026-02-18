package github.muhsenerdev.plans.application.plan.feature.add;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Schema(description = "Command to add a feature to a plan")
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Builder(toBuilder = true)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddPlanFeatureCommand {

    @JsonIgnore
    private UUID planId;

    @Schema(description = "ID of the global feature to add", example = "550e8400-e29b-41d4-a716-446655440000")
    @NotNull(message = "Feature ID is required")
    private UUID featureId;

    @Schema(description = "Value for the feature in this plan", example = "300")
    @NotBlank(message = "Feature value is required")
    private String value;
}
