package github.muhsenerdev.plans.infra.webhook;

import java.time.OffsetDateTime;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class StripeEventService {

    private final StripeEventRepository stripeEventRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public boolean tryCreate(String eventId) {
        try {
            StripeEvent event = StripeEvent.builder()
                    .id(eventId)
                    .status(StripeEventStatus.PROCESSING)
                    .build();
            stripeEventRepository.saveAndFlush(event);
            return true;
        } catch (DataIntegrityViolationException e) {
            log.warn("Stripe event {} already exists or processing. Skipping.", eventId);
            return false;
        }
    }

    @Transactional
    public void markAsDone(String eventId) {
        stripeEventRepository.findById(eventId).ifPresent(event -> {
            event.setStatus(StripeEventStatus.DONE);
            event.setProcessedAt(OffsetDateTime.now());
            stripeEventRepository.save(event);
        });
    }

    @Transactional
    public void markAsFailed(String eventId, String reason) {
        stripeEventRepository.findById(eventId).ifPresent(event -> {
            event.setStatus(StripeEventStatus.FAILED);
            event.setProcessedAt(OffsetDateTime.now());
            event.setFailReason(reason);
            stripeEventRepository.save(event);
        });
    }
}
