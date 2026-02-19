package github.muhsenerdev.plans.application.plan.price.activate_price;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActivatePriceCommand {
    @NotNull
    @Schema(description = "Plan ID")
    private UUID planId;

    @NotNull
    @Schema(description = "Price ID")
    private UUID priceId;

    @Schema(description = "Whether to override existing active price for the same interval", defaultValue = "false")
    private boolean overrideActivePrice;
}
