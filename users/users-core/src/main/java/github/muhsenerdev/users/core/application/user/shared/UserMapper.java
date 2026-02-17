package github.muhsenerdev.users.core.application.user.shared;

import java.util.Set;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import github.muhsenerdev.commons.core.vo.CommonVoMapper;
import github.muhsenerdev.users.api.application.auth.OidcUserDetails;
import github.muhsenerdev.users.core.application.user.register.RegisterUserCommand;
import github.muhsenerdev.users.core.application.user.resend_code.CodeResendResponse;
import github.muhsenerdev.users.core.domain.roles.Role;
import github.muhsenerdev.users.core.domain.users.EmailVerification;
import github.muhsenerdev.users.core.domain.users.RegistrationType;
import github.muhsenerdev.users.core.domain.users.SocialLoginDetails;
import github.muhsenerdev.users.core.domain.users.User;
import github.muhsenerdev.users.core.domain.users.UserCreationInput;

@Mapper(componentModel = "spring", uses = { CommonVoMapper.class, UserModuleVoMapper.class })
public interface UserMapper {

    @Mapping(target = "metadata", source = "command.otherDetails")
    @Mapping(target = "hashedPassword", ignore = true)
    @Mapping(target = "missingDetails", ignore = true)
    @Mapping(target = "socialLoginDetails", ignore = true)
    UserCreationInput toCreationInput(RegisterUserCommand command, RegistrationType registrationType,
            Set<Role> roles,
            boolean verified);

    @Mapping(target = "userId", source = "id")
    @Mapping(target = "roles", expression = "java(user.getRoles().stream().map(r -> r.getName()).collect(java.util.stream.Collectors.toSet()))")
    UserInfo toUserInfo(User user);

    CodeResendResponse toCodeResendResponse(EmailVerification emailVerification);

    SocialLoginDetails extractSocialLoginDetails(OidcUserDetails userDetails);
}
