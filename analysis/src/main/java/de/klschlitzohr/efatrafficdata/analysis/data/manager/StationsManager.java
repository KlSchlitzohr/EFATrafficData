package de.klschlitzohr.efatrafficdata.analysis.data.manager;

import de.klschlitzohr.efatrafficdata.api.data.model.OwnStation;
import de.klschlitzohr.efatrafficdata.api.database.repositories.StationsRepository;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;

/**
 * Created on 19.10.2021
 *
 * @author KlSchlitzohr
 */
@Getter
@Log4j2
public class StationsManager {

    private ArrayList<OwnStation> stations;
    private final StationsRepository stationsRepository;

    public StationsManager(StationsRepository stationsRepository) {
        this.stationsRepository = stationsRepository;
        this.stations = new ArrayList<>();
        new Thread(this::init).start();
    }

    private void init() {
        log.info("Loading Stations");
        stations = stationsRepository.getAllStations();
    }

    public OwnStation getStationByStopID(int id) {
        return stations.stream().filter(station -> station.getStopID() == id).findAny().get();
    }

    public OwnStation getStationByStopID(String id) {
        return getStationByStopID(Integer.parseInt(id));
    }

    public OwnStation getStationByOwnID(int id) {
        if (stations.stream().anyMatch(ownStation -> ownStation.getOwnStopID() == id)) {
            return stations.stream().filter(ownStation -> ownStation.getOwnStopID() == id).findAny().get();
        } else {
            return null;
        }
    }

}
