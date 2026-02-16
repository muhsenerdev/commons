package github.muhsenerdev.users.core.application.user.register;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.lenient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
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
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.containers.PostgreSQLContainer;

import github.muhsenerdev.commons.core.exception.DomainException;
import github.muhsenerdev.commons.core.exception.InvalidDomainException;
import github.muhsenerdev.commons.core.vo.Email;
import github.muhsenerdev.commons.core.vo.Username;
import github.muhsenerdev.users.api.application.registration.RegistrationApplicationService;
import github.muhsenerdev.users.core.domain.roles.RoleRepository;
import github.muhsenerdev.users.core.domain.users.RegistrationType;
import github.muhsenerdev.users.core.domain.users.User;
import github.muhsenerdev.users.core.domain.users.UserDSL;
import github.muhsenerdev.users.core.domain.users.UserRepository;
import github.muhsenerdev.users.core.infra.config.UserModuleProperties;
import lombok.extern.slf4j.Slf4j;

@SuppressWarnings("unchecked")
@SpringBootTest
@Sql("classpath:schema.sql")
@Slf4j
public class RegistrationApplicationServiceIT {

    @Autowired
    @SuppressWarnings("rawtypes")
    private RegistrationApplicationService service;

    @MockitoBean
    private UserModuleProperties properties;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @ServiceConnection
    static PostgreSQLContainer<?> postgresqlContainer = new PostgreSQLContainer<>("postgres:17")
            .withDatabaseName("test")
            .withUsername("test")
            .withPassword("test");

    private DefaultRegistrationCommand command;

    @BeforeEach
    void setUp() {
        command = DefaultRegistrationCommand.builder()
                .email(UUID.randomUUID().toString() + "@gmail.com")
                .password("password").build();

        lenient().when(properties.isNameRequired()).thenReturn(false);
        lenient().when(properties.isUsernameRequired()).thenReturn(false);

    }

    @Test
    @DisplayName("Happy Path")
    void happyPath() {

        // ACT
        var response = service.register(command);

        // ASSERT
        UUID userId = response.getId();
        var user = userRepository.findById(userId);

        // Assert: It is found.
        assertThat(user).isPresent();
        User userFound = user.get();

        // Assert: It is in verifying state.
        assertThat(userFound.getEmailVerification()).isNotNull();
        var verification = userFound.getEmailVerification();
        assertThat(verification.getExpiresAt()).isNotNull();
        assertThat(verification.getCode()).isNotBlank();
        assertThat(userFound.isVerifying()).isTrue();

        // Assert: It is registered with password.
        assertThat(userFound.getRegistrationType()).isNotNull();
        assertThat(userFound.getRegistrationType()).isEqualTo(RegistrationType.PASSWORD);
    }

    @Test
    @DisplayName("User Already Exists")
    void usersAlreadyExists_thenThrowsDomainException() {
        // ARRANGE: Register a user
        service.register(command);

        // ACT & ASSERT
        assertThatThrownBy(() -> service.register(command))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("Email is already taken");
    }

    @Test
    @DisplayName("Email Already Exists but Soft Deleted")
    void emailAlreadyExists_butSoftDeleted_thenOk() {
        // ARRANGE: Register a user then soft delete it.
        User user = UserDSL.aUser()
                .save(roleRepository, userRepository);
        userRepository.delete(user);
        command = command.withEmail(user.getEmail().getValue());

        // ACT
        service.register(command);

        // ASSERT
        Optional<User> userOptional = userRepository.findByEmail(user.getEmail());
        assertThat(userOptional).isPresent();
        User userFound = userOptional.get();
        assertThat(userFound.isVerifying()).isTrue();
    }

    @Test
    @DisplayName("When 10 concurrent request with the same email, then only one should be registered")
    void when10ConcurrentRequestWithTheSameEmail_thenOnlyOneShouldBeRegistered() throws InterruptedException {
        // ARRANGE
        command = command.withEmail("sameEmail@email.com");
        int threadCount = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch readyLatch = new CountDownLatch(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(threadCount);

        List<Throwable> errors = Collections.synchronizedList(new ArrayList<>());

        for (int i = 0; i < threadCount; i++) {
            executorService.submit(
                    () -> {
                        readyLatch.countDown();

                        try {
                            startLatch.await();
                            service.register(command);
                        } catch (Throwable throwable) {
                            errors.add(throwable);
                        } finally {
                            doneLatch.countDown();
                        }

                    });
        }

        readyLatch.await();

        startLatch.countDown();

        doneLatch.await();
        executorService.shutdown();

        long successCount = threadCount - errors.size();

        userRepository.findByEmail(Email.of(command.getEmail()));

        assertThat(successCount).isEqualTo(1);
        assertThat(errors.size()).isEqualTo(threadCount - 1);
    }

    @Test
    @DisplayName("Given username is required, when 10 concurrent request with the same username, then only one should be registered")
    void givenUsernameIsRequired_when10ConcurrentRequestWithTheSameUsername_thenOnlyOneShouldBeRegistered()
            throws InterruptedException {
        // ARRANGE
        lenient().when(properties.isUsernameRequired()).thenReturn(true);

        int threadCount = 10;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch readyLatch = new CountDownLatch(threadCount);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch doneLatch = new CountDownLatch(threadCount);

        List<Throwable> errors = Collections.synchronizedList(new ArrayList<>());

        for (int i = 0; i < threadCount; i++) {
            executorService.submit(
                    () -> {
                        readyLatch.countDown();

                        try {
                            startLatch.await();
                            service.register(DefaultRegistrationCommand.builder()
                                    .email(UUID.randomUUID() + "@gmail.com")
                                    .username("sameUsername")
                                    .password(command.getPassword())
                                    .build());
                        } catch (Throwable throwable) {
                            errors.add(throwable);
                        } finally {
                            doneLatch.countDown();
                        }

                    });
        }

        readyLatch.await();

        startLatch.countDown();

        doneLatch.await();
        executorService.shutdown();

        long successCount = threadCount - errors.size();

        userRepository.findByUsername(Username.of("sameUsername"));

        assertThat(successCount).isEqualTo(1);
        assertThat(errors.size()).isEqualTo(9);
        for (Throwable err : errors) {
            log.debug("Error: {}", err.getMessage());
        }
    }

    @Test
    @DisplayName("Given username is mandatory, when username is not provided, then throws InvalidDomainException")
    void givenUsernameIsMandatory_whenUsernameIsNotProvided_thenThrowsInvalidDomainException() {
        // ARRANGE
        lenient().when(properties.isUsernameRequired()).thenReturn(true);

        // ACT & ASSERT
        assertThatThrownBy(() -> service.register(command))
                .isInstanceOf(InvalidDomainException.class)
                .hasFieldOrPropertyWithValue("code", "username.invalid");
    }

    @Test
    @DisplayName("Given name is mandatory, when name is not provided, then throws InvalidDomainException")
    void givenNameIsMandatory_whenNameIsNotProvided_thenThrowsInvalidDomainException() {
        // ARRANGE
        lenient().when(properties.isNameRequired()).thenReturn(true);

        // ACT & ASSERT
        assertThatThrownBy(() -> service.register(command))
                .isInstanceOf(InvalidDomainException.class)
                .hasFieldOrPropertyWithValue("code", "name.invalid");
    }

    @Test
    @DisplayName("GIVEN username is mandatory. WHEN username has been taken, then throws DomainException")
    void givenUsernameIsMandatory_whenUsernameHasBeenTaken_thenThrowsDomainException() {
        // ARRANGE
        lenient().when(properties.isUsernameRequired()).thenReturn(true);
        User user = UserDSL.aUser()
                .withUsername("aUserName-" + UUID.randomUUID())
                .save(roleRepository, userRepository);
        command = command.withUsername(user.getUsername().getValue());

        // ACT & ASSERT
        assertThatThrownBy(() -> service.register(command))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("Username is already taken");
    }
}
