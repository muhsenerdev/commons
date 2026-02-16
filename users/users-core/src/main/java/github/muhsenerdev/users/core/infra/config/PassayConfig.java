package github.muhsenerdev.users.core.infra.config;

import org.passay.PasswordValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PassayConfig {

    @Bean
    public PasswordValidator passwordValidator() {
        return new PasswordValidator();
    }

}
