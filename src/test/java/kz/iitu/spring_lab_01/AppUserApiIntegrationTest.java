package kz.iitu.spring_lab_01;

import kz.iitu.spring_lab_01.catalog.AppUserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
@Testcontainers
class AppUserApiIntegrationTest {

    @Container
    @ServiceConnection
    static PostgreSQLContainer postgres = new PostgreSQLContainer("postgres:17-alpine");

    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    AppUserRepository users;

    @Test
    void flywayMigrationsPopulateAppUsersInRealPostgres() {
        var admin = users.findByUsername("admin");
        assertThat(admin).isPresent();
        assertThat(admin.get().getRoles()).contains("ADMIN");
        assertThat(admin.get().getPasswordHash()).startsWith("$2a$");
    }

    @Test
    void unauthenticatedRequestToBooksDoesNotLeakCatalogue() {
        ResponseEntity<String> response = restTemplate.getForEntity("/api/books", String.class);

        assertThat(response.getBody())
                .as("unauthenticated request must not see the books list")
                .doesNotContain("Effective Java", "Clean Code", "Spring in Action");
    }
}