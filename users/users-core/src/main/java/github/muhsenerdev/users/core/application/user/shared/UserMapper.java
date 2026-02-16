package github.muhsenerdev.users.core.application.user.shared;

import java.util.Set;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import github.muhsenerdev.commons.core.vo.CommonVoMapper;
import github.muhsenerdev.users.api.application.registration.EmailVerifiedEvent;
import github.muhsenerdev.users.api.application.registration.RegisterUserBaseCommand;
import github.muhsenerdev.users.api.application.registration.UserRegisteredEvent;
import github.muhsenerdev.users.api.application.registration.VerificationCodeResentEvent;
import github.muhsenerdev.users.core.application.user.resend_code.CodeResendResponse;
import github.muhsenerdev.users.core.domain.roles.Role;
import github.muhsenerdev.users.core.domain.users.EmailVerification;
import github.muhsenerdev.users.core.domain.users.RegistrationType;
import github.muhsenerdev.users.core.domain.users.User;
import github.muhsenerdev.users.core.domain.users.UserCreationInput;

@Mapper(componentModel = "spring", uses = { CommonVoMapper.class })
public interface UserMapper {

    @Mapping(target = "metadata", expression = "java(command.fetchMetadata())")
    UserCreationInput toCreationInput(RegisterUserBaseCommand command, RegistrationType registrationType,
            Set<Role> roles,
            boolean verified);

    @Mapping(target = "userId", source = "id")
    @Mapping(target = "verificationExpiresAt", source = "emailVerification.expiresAt")
    @Mapping(target = "verificationCode", source = "emailVerification.code")
    UserRegisteredEvent toUserRegisteredEvent(User user);

    @Mapping(target = "userId", source = "id")
    @Mapping(target = "verificationExpiresAt", source = "emailVerification.expiresAt")
    @Mapping(target = "verificationCode", source = "emailVerification.code")
    VerificationCodeResentEvent toVerificationCodeResentEvent(User user);

    @Mapping(target = "userId", source = "id")
    @Mapping(target = "roles", expression = "java(user.getRoles().stream().map(r -> r.getName().getValue()).collect(java.util.stream.Collectors.toSet()))")
    EmailVerifiedEvent toEmailVerifiedEvent(User user);



    CodeResendResponse toCodeResendResponse(EmailVerification emailVerification);
}
