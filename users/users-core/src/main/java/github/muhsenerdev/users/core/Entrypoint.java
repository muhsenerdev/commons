package github.muhsenerdev.users.core;

import java.util.List;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.testcontainers.containers.PostgreSQLContainer;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
@EnableJpaAuditing
@EnableAsync
@EnableScheduling
@ComponentScan(basePackages = { "github.muhsenerdev" })
public class Entrypoint {

    public static void main(String[] args) {
        SpringApplication.run(Entrypoint.class, args);
    }

    @ServiceConnection
    @Bean
    public PostgreSQLContainer<?> postgresqlContainer() {
        var container = new PostgreSQLContainer<>("postgres:17")
                .withDatabaseName("users-test")
                .withUsername("postgres")
                .withPassword("postgres");

        container.setPortBindings(List.of("5432:5432"));

        return container;
    }

}
