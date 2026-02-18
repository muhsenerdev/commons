package github.muhsenerdev.plans.application.plan.price.delete_price;

import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class DeletePriceCommand {

    @JsonIgnore
    @Schema(hidden = true)
    private UUID planId;

    @JsonIgnore
    @Schema(hidden = true)
    private UUID priceId;
}
