package at.htlstp.aslan.houserent;

import static org.assertj.core.api.Assertions.assertThat;

import at.htlstp.aslan.houserent.service.HouseService;
import at.htlstp.aslan.houserent.service.RentalService;
import at.htlstp.aslan.houserent.service.StationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("local")
class HouseRentApplicationTests {

    @Autowired
    private StationService stationService;

    @Autowired
    private HouseService houseService;

    @Autowired
    private RentalService rentalService;

    @Test
    void contextLoadsAndMigrationsAreApplied() {
        assertThat(stationService.findAll()).hasSize(4);
        assertThat(houseService.findAll()).hasSize(10);
        assertThat(rentalService.findRunningRentals()).hasSize(1);
    }
}
