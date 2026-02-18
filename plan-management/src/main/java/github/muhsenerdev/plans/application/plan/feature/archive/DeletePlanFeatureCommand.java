package github.muhsenerdev.plans.application.plan.feature.archive;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Builder(toBuilder = true)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeletePlanFeatureCommand {

    @JsonIgnore
    private UUID planId;

    @JsonIgnore
    private UUID planFeatureId;
}
