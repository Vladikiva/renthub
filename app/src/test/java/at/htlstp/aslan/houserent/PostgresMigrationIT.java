package at.htlstp.aslan.houserent;

import static org.assertj.core.api.Assertions.assertThat;

import at.htlstp.aslan.houserent.service.HouseService;
import at.htlstp.aslan.houserent.service.RentalService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 * Runs the Flyway migrations and the JPA schema validation against the same
 * PostgreSQL version that is used in production.
 */
@SpringBootTest
@Testcontainers
class PostgresMigrationIT {

    @Container
    static final PostgreSQLContainer<?> POSTGRES = new PostgreSQLContainer<>("postgres:16-alpine");

    @Autowired
    private HouseService houseService;

    @Autowired
    private RentalService rentalService;

    @DynamicPropertySource
    static void datasourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
    }

    @Test
    void schemaMatchesTheEntitiesAndDemoDataIsLoaded() {
        assertThat(houseService.findAll()).hasSize(10);
        assertThat(rentalService.findRunningRentals()).hasSize(1);
    }
}
