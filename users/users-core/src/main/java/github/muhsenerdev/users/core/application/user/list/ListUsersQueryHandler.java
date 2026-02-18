package github.muhsenerdev.users.core.application.user.list;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import github.muhsenerdev.users.core.domain.users.UserReadRepository;
import github.muhsenerdev.users.core.domain.users.UserSpecs;
import github.muhsenerdev.users.core.application.user.shared.UserMapper;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ListUsersQueryHandler {

    private final UserReadRepository userReadRepository;
    private final UserMapper userMapper;

    @Transactional(readOnly = true)
    public Page<UserListItem> handle(ListUsersQuery query) {
        var spec = UserSpecs.create(query.getFilter());

        return userReadRepository.findBy(spec, q -> q
                .project("roles")
                .page(query.getPageable()))
                .map(userMapper::toListItem);
    }
}
