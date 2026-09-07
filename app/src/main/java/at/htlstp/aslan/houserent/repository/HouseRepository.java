package at.htlstp.aslan.houserent.repository;

import at.htlstp.aslan.houserent.model.House;
import at.htlstp.aslan.houserent.model.Station;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HouseRepository extends JpaRepository<House, String> {
    List<House> findByStation(Station station);

    // List<House> findByStreetGreaterThan(String street);
}
