package github.muhsenerdev.users.core.application.user.change_roles;

import java.util.Set;
import java.util.UUID;

import github.muhsenerdev.commons.core.vo.RoleName;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PutUserRolesCommand {
    @NotNull
    private UUID userId;

    @NotEmpty
    private Set<RoleName> roles;
}
