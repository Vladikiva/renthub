package at.htlstp.aslan.houserent.service;

import at.htlstp.aslan.houserent.model.House;
import at.htlstp.aslan.houserent.model.Station;
import at.htlstp.aslan.houserent.repository.HouseRepository;
import at.htlstp.aslan.houserent.repository.RentalRepository;
import at.htlstp.aslan.houserent.util.MessagesBean;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class HouseService {

    private final MessagesBean messages;
    private final HouseRepository houseRepository;
    private final RentalRepository rentalRepository;
    private final StationService stationService;

    public HouseService(
            MessagesBean messages,
            HouseRepository houseRepository,
            RentalRepository rentalRepository,
            StationService stationService) {
        this.messages = messages;
        this.houseRepository = houseRepository;
        this.rentalRepository = rentalRepository;
        this.stationService = stationService;
    }

    /** Returns all houses that belong to the given station. */
    public List<House> findByStation(Station station) {
        return houseRepository.findByStation(station);
    }

    public List<House> findAll() {
        return houseRepository.findAll();
    }

    /**
     * Saves the given house.
     *
     * @throws IllegalArgumentException if no station was provided
     * @throws EntityNotFoundException if the provided station does not exist
     * @throws EntityExistsException if the house number is already taken
     */
    public House create(House house) {
        if (house.getStation() == null) {
            throw new IllegalArgumentException(messages.get("houseStationNotNull"));
        }
        if (house.getStation().getId() == null
                || !stationService.existsById(house.getStation().getId())) {
            throw new EntityNotFoundException(messages.get("stationNotFound"));
        }
        if (houseRepository.existsById(house.getHouseNr())) {
            throw new EntityExistsException(messages.get("houseAlreadyExists"));
        }
        return houseRepository.save(house);
    }

    /**
     * Deletes the house with the given house number, see {@link #canDelete(House)}.
     *
     * @throws EntityNotFoundException if no such house exists
     * @throws IllegalArgumentException if the house is in use
     */
    public void deleteById(String houseNr) {
        House house = houseRepository
                .findById(houseNr)
                .orElseThrow(() -> new EntityNotFoundException(messages.get("houseNotFound")));
        if (!canDelete(house)) {
            throw new IllegalArgumentException(messages.get("houseDeleteError"));
        }
        houseRepository.delete(house);
    }

    /**
     * A house can be deleted when it currently sits at a station (it is not rented
     * out) and was never part of a rental.
     */
    public boolean canDelete(House house) {
        return house.getStation() != null && rentalRepository.findByHouse(house).isEmpty();
    }
}
