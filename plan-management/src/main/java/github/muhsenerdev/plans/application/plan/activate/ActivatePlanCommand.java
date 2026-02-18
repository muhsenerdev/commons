package github.muhsenerdev.plans.application.plan.activate;

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
public class ActivatePlanCommand {
    @NotNull
    @Schema(description = "Plan ID", example = "550e8400-e29b-41d4-a716-446655440000")
    private UUID planId;
}
