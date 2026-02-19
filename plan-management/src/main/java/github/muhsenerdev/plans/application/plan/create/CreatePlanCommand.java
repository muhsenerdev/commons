
package github.muhsenerdev.plans.application.plan.create;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import github.muhsenerdev.plans.domain.plan.PlanType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

@Schema(description = "Command to create a new plan")
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Builder(toBuilder = true)
@Getter
public class CreatePlanCommand {

    @Schema(description = "Unique code for the plan", example = "BASIC_PLAN")
    @NotBlank(message = "Plan code is required")
    @Size(min = 3, max = 50, message = "Plan code must be between 3 and 50 characters")
    private final String code;

    @Schema(description = "Detailed description of the plan", example = "The basic plan for individual users")
    @Size(min = 3, max = 500, message = "Plan description must be between 3 and 500 characters, if provided.")
    private final String description;

    @Schema(description = "Short title of the plan", example = "Basic")
    @NotBlank(message = "Plan title is required")
    @Size(min = 3, max = 255, message = "Plan title must be between 3 and 255 characters")
    private final String title;

    @Schema(description = "Display name of the plan", example = "Basic Subscription")
    @NotBlank(message = "Plan name is required")
    @Size(min = 3, max = 255, message = "Plan name must be between 3 and 255 characters")
    private final String name;

    @Schema(description = "Type of the plan", example = "PAID", implementation = PlanType.class)
    @NotNull(message = "Plan type is required")
    private final String type;

    @Schema(description = "Hierarchy level of the plan", example = "1")
    @Positive(message = "Plan tier must be positive.")
    private final int tier;

}
