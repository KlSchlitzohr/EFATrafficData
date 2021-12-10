package de.klschlitzohr.efatrafficdata.api.database.repositories;

import de.klschlitzohr.efatrafficdata.api.data.model.OwnStation;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created on 07.12.2021
 *
 * @author DerMistkaefer
 */
public interface StationsRepository {

    ArrayList<OwnStation> getAllStations();
    Map<String,Integer> getAllStationsManuell();
    int insertStation(OwnStation station);
    void updateXY(int ownStopID, double xFlat, double yFlat);
    void updateNames(int ownStopID, String name, String nameWithPlace);
}
