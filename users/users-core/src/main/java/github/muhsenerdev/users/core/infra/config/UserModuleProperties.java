package github.muhsenerdev.users.core.infra.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@ConfigurationProperties(prefix = "app.users")
@Component
public class UserModuleProperties {
    private Requirements requirements = new Requirements();
    private Admin admin = new Admin();

    @Data
    public static class Requirements {
        private boolean usernameRequired = false;
        private boolean nameRequired = false;
    }

    @Data
    public static class Admin {
        private String email;
        private String password;
    }

    private Watchdog watchdog = new Watchdog();

    @Data
    public static class Watchdog {
        private java.time.Duration registrationCleanupInterval = java.time.Duration.ofMinutes(5);
    }

    public boolean isUsernameRequired() {
        return requirements.isUsernameRequired();
    }

    public boolean isNameRequired() {
        return requirements.isNameRequired();
    }

    public java.time.Duration getRegistrationCleanupInterval() {
        return watchdog.getRegistrationCleanupInterval();
    }
}
