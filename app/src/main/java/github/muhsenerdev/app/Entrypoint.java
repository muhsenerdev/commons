package github.muhsenerdev.app;

import java.util.List;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.testcontainers.containers.PostgreSQLContainer;

@SpringBootApplication(scanBasePackages = "github.muhsenerdev")
@EnableAsync
@EnableScheduling
@EnableJpaAuditing
@EnableJpaRepositories(basePackages = "github.muhsenerdev")
@EntityScan(basePackages = "github.muhsenerdev")
public class Entrypoint {

    public static void main(String[] args) {
        SpringApplication.run(Entrypoint.class, args);
    }

    @ServiceConnection
    @Bean
    public PostgreSQLContainer<?> postgresqlContainer() {
        var container = new PostgreSQLContainer<>("postgres:17")
                .withDatabaseName("platform-db")
                .withUsername("postgres")
                .withPassword("postgres");

        container.setPortBindings(List.of("5432:5432"));

        return container;
    }

}
