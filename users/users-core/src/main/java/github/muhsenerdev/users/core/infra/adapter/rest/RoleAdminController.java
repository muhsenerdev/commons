package github.muhsenerdev.users.core.infra.adapter.rest;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import github.muhsenerdev.users.core.application.role.list.RoleListItem;
import github.muhsenerdev.users.core.application.role.shared.RoleApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin/roles")
@RequiredArgsConstructor
@Tag(name = "Role Administration", description = "Endpoints for managing system roles")
public class RoleAdminController {

    private final RoleApplicationService roleApplicationService;

    @GetMapping
    @Operation(summary = "List all roles", description = "Returns a list of all roles available in the system")
    public List<RoleListItem> listRoles() {
        return roleApplicationService.listRoles();
    }
}
