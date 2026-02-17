package github.muhsenerdev.users.core.infra.messaging;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import github.muhsenerdev.commons.core.vo.CommonVoMapper;
import github.muhsenerdev.users.api.application.auth.PasswordResetRequestedIntegrationEvent;
import github.muhsenerdev.users.api.application.registration.RegistrationCompletedEvent;
import github.muhsenerdev.users.api.application.registration.UserRegisteredEvent;
import github.muhsenerdev.users.api.application.registration.VerificationCodeResentEvent;
import github.muhsenerdev.users.core.application.user.shared.UserModuleVoMapper;
import github.muhsenerdev.users.core.domain.users.CodeResent;
import github.muhsenerdev.users.core.domain.users.PasswordResetRequested;
import github.muhsenerdev.users.core.domain.users.User;

@Mapper(componentModel = "spring", uses = { UserModuleVoMapper.class, CommonVoMapper.class })
public interface UserMessagingMapper {

    @Mapping(target = "userId", source = "id")
    @Mapping(target = "roles", expression = "java(user.getRoles().stream().map(r -> r.getName().getValue()).collect(java.util.stream.Collectors.toSet()))")
    RegistrationCompletedEvent toRegistrationCompletedEvent(User user);

    @Mapping(target = "userId", source = "id")
    @Mapping(target = "verificationExpiresAt", source = "emailVerification.expiresAt")
    @Mapping(target = "verificationCode", source = "emailVerification.code")
    UserRegisteredEvent toUserRegisteredEvent(User user);

    VerificationCodeResentEvent toVerificationCodeResentEvent(CodeResent codeResent);

    @Mapping(target = "userId", source = "userId")
    PasswordResetRequestedIntegrationEvent toPasswordResetRequestedIntegrationEvent(PasswordResetRequested event);
}
