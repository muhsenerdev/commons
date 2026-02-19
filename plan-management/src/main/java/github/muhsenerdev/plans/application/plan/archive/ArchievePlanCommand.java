package github.muhsenerdev.plans.application.plan.archive;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record ArchievePlanCommand(@NotNull UUID planId) {
}
