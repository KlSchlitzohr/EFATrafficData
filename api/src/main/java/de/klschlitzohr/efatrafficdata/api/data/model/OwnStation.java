package de.klschlitzohr.efatrafficdata.api.data.model;

import de.klschlitzohr.efatrafficdata.api.database.repositories.StationsRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

/**
 * Created on 19.10.2021
 *
 * @author KlSchlitzohr
 */
@Getter
@AllArgsConstructor
@Log4j2
public class OwnStation {

    private int ownStopID;
    private int stopID;
    private String gid;
    private double x;
    private double y;
    private String place;
    private String name;
    private String nameWithPlace;

    public void writeToDatabase(StationsRepository stationsRepository) {
        ownStopID = stationsRepository.insertStation(this);
        log.debug("Add Station " + stopID + " " + nameWithPlace);
    }

    public void updateXY(double xFlat, double yFlat, StationsRepository stationsRepository) {
        if (Math.abs(xFlat - x) > 0.00001 || Math.abs(yFlat - y) > 0.00001 ) {
            stationsRepository.updateXY(ownStopID, xFlat, yFlat);
        }
    }

    public double distance(OwnStation ownStation) {
        return Math.sqrt(Math.pow(this.x - ownStation.getX(),2)+Math.pow(this.y - ownStation.getY(),2));
    }

    public void updateNames(String name, String nameWithPlace, StationsRepository stationsRepository) {
        this.nameWithPlace = nameWithPlace;
        this.name = name;
        stationsRepository.updateNames(ownStopID, name, nameWithPlace);
        log.debug("Updated " + nameWithPlace);
    }

}
