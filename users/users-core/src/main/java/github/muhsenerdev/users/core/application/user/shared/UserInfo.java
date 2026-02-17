package github.muhsenerdev.users.core.application.user.shared;

import java.util.Set;
import java.util.UUID;

import github.muhsenerdev.commons.core.vo.Email;
import github.muhsenerdev.commons.core.vo.Name;
import github.muhsenerdev.commons.core.vo.RoleName;
import github.muhsenerdev.commons.core.vo.Username;
import github.muhsenerdev.users.core.domain.users.UserStatus;
import lombok.Builder;

@Builder
public record UserInfo(UUID userId, Email email, Username username, Name name, Set<RoleName> roles, UserStatus status,
        Set<String> missingDetails) {

}
