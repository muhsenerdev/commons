package github.muhsenerdev.commons.core.vo;

import github.muhsenerdev.commons.core.exception.InvalidDomainException;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RoleName extends SingleVO<String> {

    private String name;

    private RoleName(String value) {
        this.name = value;

        if (value == null || value.isBlank()) {
            throw new InvalidDomainException("Role name cannot be empty.");
        }

        if (!value.startsWith("ROLE_")) {
            this.name = "ROLE_" + value;
        }

    }

    @Builder
    public static RoleName of(String value) {
        return new RoleName(value);
    }

    public static RoleName user() {
        return new RoleName("ROLE_USER");
    }

    public static RoleName admin() {
        return new RoleName("ROLE_ADMIN");
    }

    public static RoleName editor() {
        return new RoleName("ROLE_EDITOR");
    }

    public static RoleName viewer() {
        return new RoleName("ROLE_VIEWER");
    }

    public static RoleName guest() {
        return new RoleName("ROLE_GUEST");
    }

    public static RoleName superAdmin() {
        return new RoleName("ROLE_SUPER_ADMIN");
    }

    @Override
    public String getValue() {
        return this.name;
    }

}
