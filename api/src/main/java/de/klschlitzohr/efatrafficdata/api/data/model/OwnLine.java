package de.klschlitzohr.efatrafficdata.api.data.model;

import de.klschlitzohr.efatrafficdata.api.database.repositories.LineRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;

/**
 * Created on 19.10.2021
 *
 * @author KlSchlitzohr
 */
@Getter
@AllArgsConstructor
@Log4j2
public class OwnLine {

    @Setter
    private int ownLineID;
    private String stateless;
    private String name;
    private String number;
    private String operator;
    private ArrayList<OwnLineStop> ownLineStops;

    public OwnLine(int ownLineID, String stateless, String name, String number, String operator) {
        this.ownLineID = ownLineID;
        this.stateless = stateless;
        this.name = name;
        this.number = number;
        this.operator = operator;
        this.ownLineStops = new ArrayList<>();
    }

    public void addStop(OwnLineStop ownLineStop, LineRepository lineRepository) {
        if (ownLineStop.getLineID() != ownLineID)
            return;
        for (OwnLineStop stop : ownLineStops) {
            if (ownLineStop.getStopID() == stop.getStopID() && ownLineStop.getDepartureDelay() == stop.getDepartureDelay()) {
                return;
            }
        }
        ownLineStops.add(ownLineStop);
        lineRepository.insertLineStop(ownLineStop);
        log.debug("Add lineStop " + stateless + " " + name + " " + operator);
    }

    public void setPath(String path, LineRepository lineRepository) {
        lineRepository.insertPath(this, path);
    }

}
