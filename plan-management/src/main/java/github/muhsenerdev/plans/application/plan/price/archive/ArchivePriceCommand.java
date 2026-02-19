package github.muhsenerdev.plans.application.plan.price.archive;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ArchivePriceCommand {

    @NotNull
    private UUID planId;

    @NotNull
    private UUID priceId;

}
