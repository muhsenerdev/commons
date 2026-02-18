package github.muhsenerdev.users.core.application.role.shared;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import github.muhsenerdev.commons.core.vo.CommonVoMapper;
import github.muhsenerdev.users.core.application.role.list.RoleListItem;
import github.muhsenerdev.users.core.application.user.shared.UserModuleVoMapper;
import github.muhsenerdev.users.core.domain.roles.Role;

@Mapper(componentModel = "spring", uses = { CommonVoMapper.class, UserModuleVoMapper.class })
public interface RoleMapper {

    @Mapping(target = "name", source = "name.value")
    RoleListItem toListItem(Role role);

    List<RoleListItem> toListItems(List<Role> roles);
}
