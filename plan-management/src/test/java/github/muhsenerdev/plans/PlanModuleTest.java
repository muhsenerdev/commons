package github.muhsenerdev.plans;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "github.muhsenerdev.plans")
@EntityScan(basePackages = "github.muhsenerdev.plans")
@EnableJpaAuditing
public class PlanModuleTest {

}
