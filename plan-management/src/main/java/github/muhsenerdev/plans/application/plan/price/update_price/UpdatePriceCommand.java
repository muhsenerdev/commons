// package github.muhsenerdev.plans.application.plan.price.update_price;

// import java.math.BigDecimal;
// import java.util.UUID;

// import com.fasterxml.jackson.annotation.JsonIgnore;
// import com.fasterxml.jackson.databind.PropertyNamingStrategies;
// import com.fasterxml.jackson.databind.annotation.JsonNaming;

// import github.muhsenerdev.plans.domain.shared.Interval;
// import io.swagger.v3.oas.annotations.media.Schema;
// import jakarta.validation.constraints.NotBlank;
// import jakarta.validation.constraints.NotNull;
// import jakarta.validation.constraints.Positive;
// import lombok.Builder;
// import lombok.Getter;
// import lombok.Setter;

// @Schema(description = "Command to update a plan price")
// @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
// @Builder(toBuilder = true)
// @Getter
// @Setter
// public class UpdatePriceCommand {

// @JsonIgnore
// @Schema(hidden = true)
// private UUID planId;

// @JsonIgnore
// @Schema(hidden = true)
// private UUID priceId;

// @Schema(description = "Price amount", example = "29.99")
// @NotNull(message = "Amount is required")
// @Positive(message = "Amount must be positive")
// private BigDecimal amount;

// @Schema(description = "Currency of the price", example = "USD")
// @NotBlank(message = "Currency is required")
// private String currency;

// @Schema(description = "Billing interval", example = "YEARLY")
// @NotNull(message = "Interval is required")
// private Interval interval;
// }
