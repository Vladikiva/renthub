package at.htlstp.aslan.houserent.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import at.htlstp.aslan.houserent.bean.FinishRentalBean;
import at.htlstp.aslan.houserent.model.House;
import at.htlstp.aslan.houserent.model.Rental;
import at.htlstp.aslan.houserent.model.Station;
import at.htlstp.aslan.houserent.repository.HouseRepository;
import at.htlstp.aslan.houserent.repository.RentalRepository;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RentalServiceTest {

    @Mock
    private RentalRepository rentalRepository;

    @Mock
    private HouseRepository houseRepository;

    private RentalService rentalService;

    @BeforeEach
    void setUp() {
        rentalService = new RentalService(rentalRepository, houseRepository, new SimpleMeterRegistry());
    }

    @Test
    void rentalCanOnlyBeCreatedWhenTheHouseBelongsToTheStation() {
        Station station = new Station(1, "Москва");
        House house = house("H1", station);
        Rental rental = rental(house, station);

        when(houseRepository.findByStation(station)).thenReturn(List.of(house));
        assertThat(rentalService.canCreate(rental)).isTrue();

        when(houseRepository.findByStation(station)).thenReturn(List.of());
        assertThat(rentalService.canCreate(rental)).isFalse();
    }

    @Test
    void rentalCanBeFinishedOnlyWhileItIsStillRunning() {
        Rental running = rental(house("H1", null), new Station(1, "Москва"));
        assertThat(rentalService.canFinish(running)).isTrue();

        running.setKm(100);
        assertThat(rentalService.canFinish(running)).isFalse();
    }

    @Test
    void returnDateBeforeRentalDateIsRejected() {
        Rental rental = rental(house("H1", null), new Station(1, "Москва"));
        rental.setRentalDate(LocalDate.of(2024, 5, 10));

        FinishRentalBean tooEarly = finishBean(LocalDate.of(2024, 5, 9));
        assertThat(rentalService.cleanDates(rental, tooEarly)).isFalse();
        assertThat(rental.getReturnDate()).isNull();

        FinishRentalBean valid = finishBean(LocalDate.of(2024, 5, 12));
        assertThat(rentalService.cleanDates(rental, valid)).isTrue();
        assertThat(rental.getReturnDate()).isEqualTo(LocalDate.of(2024, 5, 12));
    }

    @Test
    void existsAndCanFinishReturnsEmptyForUnknownId() {
        assertThat(rentalService.existsAndCanFinish(null)).isEmpty();
    }

    private static House house(String houseNr, Station station) {
        House house = new House();
        house.setHouseNr(houseNr);
        house.setPrice(20000);
        house.setStreet("street");
        house.setModel("1");
        house.setStation(station);
        return house;
    }

    private static Rental rental(House house, Station station) {
        Rental rental = new Rental();
        rental.setRentalDate(LocalDate.now());
        rental.setHouse(house);
        rental.setRentalStation(station);
        return rental;
    }

    private static FinishRentalBean finishBean(LocalDate returnDate) {
        FinishRentalBean bean = new FinishRentalBean();
        bean.setReturnDate(returnDate);
        return bean;
    }
}
