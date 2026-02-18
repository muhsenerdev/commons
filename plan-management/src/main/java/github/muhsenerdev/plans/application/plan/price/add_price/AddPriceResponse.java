package github.muhsenerdev.plans.application.plan.price.add_price;

import java.util.UUID;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Schema(description = "Response of adding a price to a plan")
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@Builder(toBuilder = true)
@Getter
@Setter
public class AddPriceResponse {
    @Schema(description = "ID of the added price", example = "123e4567-e89b-12d3-a456-426614174000")
    private UUID planPriceId;
}
