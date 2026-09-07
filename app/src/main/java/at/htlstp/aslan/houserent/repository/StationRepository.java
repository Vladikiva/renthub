package at.htlstp.aslan.houserent.repository;

import at.htlstp.aslan.houserent.model.Station;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StationRepository extends JpaRepository<Station, Integer> {}
