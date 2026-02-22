package github.muhsenerdev.plans.application.subscription.checkout;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.UUID;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Schema(description = "Request to start a checkout process for a plan")
public class StartCheckoutCommand {

    @NotNull
    @Schema(description = "ID of the plan to subscribe to", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID planId;

    @NotNull
    @Schema(description = "ID of the specific price/interval to subscribe to", example = "550e8400-e29b-41d4-a716-446655440001")
    private UUID priceId;

    @Schema(hidden = true)
    private UUID userId;
}
