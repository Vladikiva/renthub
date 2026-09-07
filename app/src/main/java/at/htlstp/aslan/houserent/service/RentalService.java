package at.htlstp.aslan.houserent.service;

import at.htlstp.aslan.houserent.bean.FinishRentalBean;
import at.htlstp.aslan.houserent.model.House;
import at.htlstp.aslan.houserent.model.Rental;
import at.htlstp.aslan.houserent.repository.HouseRepository;
import at.htlstp.aslan.houserent.repository.RentalRepository;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class RentalService {

    private static final Logger log = LoggerFactory.getLogger(RentalService.class);

    private final RentalRepository rentalRepository;
    private final HouseRepository houseRepository;
    private final Counter rentalsCreated;
    private final Counter rentalsFinished;
    private final Timer rentalCreationTimer;

    public RentalService(
            RentalRepository rentalRepository, HouseRepository houseRepository, MeterRegistry meterRegistry) {
        this.rentalRepository = rentalRepository;
        this.houseRepository = houseRepository;
        this.rentalsCreated = Counter.builder("renthub.rentals.created")
                .description("Number of rentals that were successfully created")
                .register(meterRegistry);
        this.rentalsFinished = Counter.builder("renthub.rentals.finished")
                .description("Number of rentals that were successfully finished")
                .register(meterRegistry);
        this.rentalCreationTimer = Timer.builder("renthub.rentals.creation")
                .description("Latency of the rental creation use case")
                .publishPercentileHistogram()
                .register(meterRegistry);
        meterRegistry.gauge("renthub.rentals.running", this, service -> service.rentalRepository
                .findRunningRentals()
                .size());
    }

    /**
     * Saves the given rental. Id, km, return date, return station and the station of
     * the house are reset, because the house is on the road from now on.
     */
    public void create(Rental rental) {
        rentalCreationTimer.record(() -> {
            rental.setId(null);
            rental.setKm(null);
            rental.setReturnDate(null);
            rental.setReturnStation(null);
            rental.getHouse().setStation(null);

            rentalRepository.save(rental);
            rentalsCreated.increment();
            log.info(
                    "rental created house={} station={}",
                    rental.getHouse().getHouseNr(),
                    rental.getRentalStation().getId());
        });
    }

    /** Finishes the given rental with the help of a {@link FinishRentalBean}. */
    public void finish(Rental rental, FinishRentalBean finishRentalBean) {
        rental.setReturnStation(finishRentalBean.getReturnStation());
        rental.setKm(finishRentalBean.getKm());
        rental.getHouse().setStation(rental.getReturnStation());

        rentalRepository.save(rental);
        rentalsFinished.increment();
        log.info("rental finished id={} km={}", rental.getId(), rental.getKm());
    }

    /** Returns all rentals that were not returned yet. */
    public List<Rental> findRunningRentals() {
        return rentalRepository.findRunningRentals();
    }

    /** Returns all rentals of a specific house. */
    public List<Rental> findByHouse(House house) {
        return rentalRepository.findByHouse(house);
    }

    /**
     * Returns the rental with the given id when it exists and can still be finished,
     * an empty {@link Optional} otherwise.
     */
    public Optional<Rental> existsAndCanFinish(Integer id) {
        if (id == null) {
            return Optional.empty();
        }
        return rentalRepository.findById(id).filter(this::canFinish);
    }

    /** A rental can be finished as long as return date, km and return station are unset. */
    public boolean canFinish(Rental rental) {
        return rental.getReturnDate() == null && rental.getKm() == null && rental.getReturnStation() == null;
    }

    /**
     * A rental can only be created when the requested house really belongs to the
     * requested station; clients can tamper with the form values.
     */
    public boolean canCreate(Rental rental) {
        return houseRepository.findByStation(rental.getRentalStation()).contains(rental.getHouse());
    }

    /**
     * Returns false when the rental date is after the return date of the bean,
     * otherwise the return date is copied onto the rental and true is returned.
     */
    public boolean cleanDates(Rental rental, FinishRentalBean finishRentalBean) {
        if (rental.getRentalDate().isAfter(finishRentalBean.getReturnDate())) {
            return false;
        }
        rental.setReturnDate(finishRentalBean.getReturnDate());
        return true;
    }
}
