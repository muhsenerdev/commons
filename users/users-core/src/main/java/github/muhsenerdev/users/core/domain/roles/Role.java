package github.muhsenerdev.users.core.domain.roles;

import github.muhsenerdev.commons.core.vo.RoleName;
import github.muhsenerdev.commons.jpa.entity.BaseJpaEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Role extends BaseJpaEntity {

    @Column(unique = true, nullable = false)
    @Embedded
    private RoleName name;

    public static Role create(RoleName roleName) {
        return new Role(roleName);
    }

    public static Role createAdminRole() {
        return new Role(RoleName.admin());
    }

    public static Role createUserRole() {
        return new Role(RoleName.user());
    }
}
