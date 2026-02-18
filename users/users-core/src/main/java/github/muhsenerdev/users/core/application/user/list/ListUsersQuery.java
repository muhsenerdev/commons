package github.muhsenerdev.users.core.application.user.list;

import org.springframework.data.domain.Pageable;

import github.muhsenerdev.users.core.domain.users.UserFilter;
import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ListUsersQuery {
    UserFilter filter;
    Pageable pageable;
}
