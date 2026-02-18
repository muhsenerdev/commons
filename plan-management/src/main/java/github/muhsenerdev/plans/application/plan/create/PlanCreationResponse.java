package github.muhsenerdev.plans.application.plan.create;

import java.util.UUID;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class PlanCreationResponse {
    private UUID id;
}
