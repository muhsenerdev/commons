package github.muhsenerdev.users.core.infra.security;

import org.mapstruct.Mapper;

import github.muhsenerdev.users.core.application.auth.DefaultPrincipal;
import github.muhsenerdev.users.core.application.user.shared.UserInfo;

@Mapper(componentModel = "spring")
public interface SecurityMapper {

    DefaultPrincipal toPrincipal(UserInfo userInfo);
}
