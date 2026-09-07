package at.htlstp.aslan.houserent.service;

import at.htlstp.aslan.houserent.model.Station;
import at.htlstp.aslan.houserent.repository.StationRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class StationService {

    private final StationRepository stationRepository;

    public StationService(StationRepository stationRepository) {
        this.stationRepository = stationRepository;
    }

    public List<Station> findAll() {
        return stationRepository.findAll();
    }

    public boolean existsById(Integer id) {
        return stationRepository.existsById(id);
    }
}
