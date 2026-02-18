package github.muhsenerdev.users.core.application.role.shared;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import github.muhsenerdev.users.core.application.role.list.RoleListItem;
import github.muhsenerdev.users.core.domain.roles.RoleRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RoleApplicationServiceImpl implements RoleApplicationService {

    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;

    @Override
    @Transactional(readOnly = true)
    public List<RoleListItem> listRoles() {
        var roles = roleRepository.findAll();
        return roleMapper.toListItems(roles);
    }
}
