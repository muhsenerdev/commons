package github.muhsenerdev.users.core.application.user.shared;

import java.util.Set;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import github.muhsenerdev.commons.core.vo.CommonVoMapper;
import github.muhsenerdev.users.api.application.registration.RegisterUserBaseCommand;
import github.muhsenerdev.users.api.application.registration.UserRegisteredEvent;
import github.muhsenerdev.users.core.domain.roles.Role;
import github.muhsenerdev.users.core.domain.users.RegistrationType;
import github.muhsenerdev.users.core.domain.users.User;
import github.muhsenerdev.users.core.domain.users.UserCreationInput;

@Mapper(componentModel = "spring", uses = { CommonVoMapper.class })
public interface UserMapper {

    UserCreationInput toCreationInput(RegisterUserBaseCommand command, RegistrationType registrationType,
            Set<Role> roles,
            boolean verified);

    @Mapping(target = "userId", source = "id")
    @Mapping(target = "verificationExpiresAt", source = "emailVerification.expiresAt")
    @Mapping(target = "verificationCode", source = "emailVerification.code")
    UserRegisteredEvent toUserRegisteredEvent(User user);
}
