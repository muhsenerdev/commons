package github.muhsenerdev.plans.infra.webhook;

import java.time.OffsetDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "stripe_events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StripeEvent {

    @Id
    private String id;

    @Column(name = "processed_at")
    private OffsetDateTime processedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StripeEventStatus status;

    @Column(name = "fail_reason", columnDefinition = "TEXT")
    private String failReason;
}
