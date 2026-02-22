package github.muhsenerdev.plans.application.entitlement;

import java.time.Duration;
import java.util.UUID;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import github.muhsenerdev.plans.domain.entitlement.EntitlementSourceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class GrantEntitlementCommand {

    private UUID userId;
    private String featureCode;
    private Duration duration;
    private Integer value;
    private EntitlementSourceType sourceType;

}
