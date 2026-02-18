package github.muhsenerdev.plans.application.plan.shared;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.containers.PostgreSQLContainer;

import github.muhsenerdev.commons.core.util.RandomUtil;
import github.muhsenerdev.plans.application.plan.create.CreatePlanCommand;
import github.muhsenerdev.plans.domain.plan.PlanDSL;
import github.muhsenerdev.plans.domain.plan.PlanDomainException;
import github.muhsenerdev.plans.domain.plan.PlanRepository;
import github.muhsenerdev.plans.domain.plan.PlanType;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest(properties = "spring.jpa.hibernate.ddl-auto=update")
@ActiveProfiles("test")
@Sql({ "classpath:schema.sql" })
public class PlanApplicationServiceTest {

    @ServiceConnection
    static PostgreSQLContainer<?> postgresqlContainer = new PostgreSQLContainer<>("postgres:17")
            .withDatabaseName("test")
            .withUsername("test")
            .withPassword("test");

    @Autowired
    private PlanApplicationService service;

    @Autowired
    private PlanRepository planRepository;

    private CreatePlanCommand command;

    @BeforeEach
    void setUp() {
        command = CreatePlanCommand.builder()
                .name("name")
                .description("description")
                .tier(RandomUtil.randomInt(10, 10000))
                .code("code" + UUID.randomUUID().toString())
                .title("Test plan title")
                .type(PlanType.PAID)
                .build();
    }

    @Test
    @DisplayName("Happy Path")
    void happyPath() {
        // ARRANGE

        // ACT
        var response = service.createPlan(command);

        // ASSERT
        var planOpt = planRepository.findById(response.getId());

        assertThat(planOpt.isPresent()).isTrue();
        assertThat(planOpt.get().isDraft()).isTrue();

    }

    @Test
    @DisplayName("Duplicated Plan Tier")
    void givenTier10AlreadyExists_whenPlanWithTier10IsCreated_thenThrowException() {
        // ARRANGE
        PlanDSL.draftPlan().withTier(10).save(planRepository);
        command = command.toBuilder().tier(10).build();

        // ACT & ASSERT
        assertThatThrownBy(() -> service.createPlan(command)).isInstanceOf(PlanDomainException.class);
    }

    @Test
    @DisplayName("Duplicated Plan Code")
    void givenDuplicatedPlanCode_whenPlanWithDuplicatedCodeIsCreated_thenThrowException() {
        // ARRANGE
        String sameCode = "code";
        PlanDSL.draftPlan().withCode(sameCode).save(planRepository);
        command = command.toBuilder().code(sameCode).build();

        // ACT & ASSERT
        assertThatThrownBy(() -> service.createPlan(command)).isInstanceOf(PlanDomainException.class);
    }

    @Test
    @DisplayName("Concurrent Plan Creation with the same code")
    void whenConcurrentPlanCreationWithTheSameCode_thenThrowException() throws InterruptedException {
        // ARRANGE
        int threadCount = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch readyLatch = new CountDownLatch(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(threadCount);

        List<Throwable> errors = Collections.synchronizedList(new ArrayList<>());
        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
                readyLatch.countDown();
                try {
                    startLatch.await();
                    service.createPlan(command);
                } catch (Throwable e) {
                    errors.add(e);
                } finally {
                    doneLatch.countDown();
                }
            });
        }

        log.debug("waiting ready Latch");
        readyLatch.await();
        startLatch.countDown();
        doneLatch.await();
        executorService.shutdown();

        int errorCount = errors.size();
        assertThat(errorCount).isEqualTo(threadCount - 1);
    }
}
