package github.muhsenerdev.users.core.infra.messaging.out;

import java.util.UUID;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import github.muhsenerdev.users.api.application.registration.RegistrationCompletedEvent;
import github.muhsenerdev.users.api.application.registration.UserRegisteredEvent;
import github.muhsenerdev.users.api.application.registration.VerificationCodeResentEvent;
import github.muhsenerdev.users.core.domain.users.CodeResent;
import github.muhsenerdev.users.core.domain.users.UserActivatedEvent;
import github.muhsenerdev.users.core.domain.users.UserCreated;
import github.muhsenerdev.users.core.domain.users.UserRepository;
import github.muhsenerdev.users.core.infra.messaging.UserMessagingMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class IntegrationEventListener {

    private final UserRepository userRepository;
    private final UserMessagingMapper mapper;
    private final ApplicationEventPublisher eventPublisher;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onUserActivated(UserActivatedEvent event) {
        UUID userId = event.getUserIdSupplier().get();
        log.debug("Listening to  UserActivated domain event for user: {}", userId);
        userRepository.findByIdWithRoles(userId).ifPresent(user -> {
            RegistrationCompletedEvent completedEvent = mapper.toRegistrationCompletedEvent(user);
            if (completedEvent != null) {
                eventPublisher.publishEvent(completedEvent);
                log.info("RegistrationCompletedEvent published for user: {}", user.getId());
            }
        });
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onUserCreated(UserCreated event) {
        log.debug("Listening to  UserCreated domain event for user {}", event.getUserIdSupplier().get());

        if (!event.isVerified()) {
            log.debug("User is not verified, so publishing UserRegisteredEvent for user {}",
                    event.getUserIdSupplier().get());
            userRepository.findByIdWithRoles(event.getUserIdSupplier().get()).ifPresent(user -> {
                UserRegisteredEvent registeredEvent = mapper.toUserRegisteredEvent(user);
                if (registeredEvent != null) {
                    eventPublisher.publishEvent(registeredEvent);
                    log.info("UserRegisteredEvent published for user: {}", user.getId());
                }
            });
        }

    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void onCodeResent(CodeResent event) {
        log.debug("Listening to  CodeResent domain event for user: {}", event.getUserId());

        VerificationCodeResentEvent completedEvent = mapper.toVerificationCodeResentEvent(event);
        if (completedEvent != null) {
            eventPublisher.publishEvent(completedEvent);
            log.info("VerificationCodeResentEvent published for user: {}", event.getUserId());
        }
    };
}
