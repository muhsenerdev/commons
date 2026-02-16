package github.muhsenerdev.users.core.infra.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@Configuration
@ConfigurationProperties(prefix = "app.security")
public class SecurityProperties {

    private AccessToken accessToken = new AccessToken();
    private Jwt jwt = new Jwt();

    @Data
    public static class AccessToken {
        private CookieSettings cookie = new CookieSettings();

        @Data
        public static class CookieSettings {
            private String name = "access_token";
            private boolean httpOnly = true;
            private boolean secure = true;
            private String path = "/";
            private Integer maxAge;
            private String domain;
        }
    }

    @Data
    public static class Jwt {
        private String secret = "default_secret_key_at_least_32_chars_long_!!";
        private long expiration = 3600000; // 1 hour
    }
}
