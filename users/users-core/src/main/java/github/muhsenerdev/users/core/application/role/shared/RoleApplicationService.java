package github.muhsenerdev.users.core.application.role.shared;

import java.util.List;

import github.muhsenerdev.users.core.application.role.list.RoleListItem;

public interface RoleApplicationService {
    List<RoleListItem> listRoles();
}
