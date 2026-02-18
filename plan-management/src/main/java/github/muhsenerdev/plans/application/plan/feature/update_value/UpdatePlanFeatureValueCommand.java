package github.muhsenerdev.plans.application.plan.feature.update_value;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Schema(description = "Command to update a plan feature value")
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Builder(toBuilder = true)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePlanFeatureValueCommand {

    @JsonIgnore
    private UUID planId;

    @JsonIgnore
    private UUID planFeatureId;

    @Schema(description = "New value for the feature", example = "500")
    @NotBlank(message = "Feature value is required")
    private String value;
}
