package de.klschlitzohr.efatrafficdata.scraper.data.manager;

import de.klschlitzohr.efatrafficdata.api.data.model.OwnStation;
import de.klschlitzohr.efatrafficdata.api.database.repositories.StationsRepository;
import de.klschlitzohr.efatrafficdata.scraper.parsing.xmlstopfinder.ItdOdvAssignedStop;
import de.klschlitzohr.efatrafficdata.scraper.parsing.xmlstopfinder.Point;
import de.klschlitzohr.efatrafficdata.scraper.parsing.xmlstopfinder.XMLSTOPFINDER;
import de.klschlitzohr.efatrafficdata.scraper.parsing.xsltdm.ItdOdvAssignedStops;
import de.klschlitzohr.efatrafficdata.scraper.parsing.xsltdm.XSLTDM;
import de.klschlitzohr.efatrafficdata.scraper.request.XMLStopFinderRequest;
import de.klschlitzohr.efatrafficdata.scraper.request.XSLTDepartureMonitorRequest;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Created on 19.10.2021
 *
 * @author KlSchlitzohr
 */
@Getter
public class StationsManager {

    private final StationsRepository stationsRepository;
    private ArrayList<OwnStation> stations;
    private Map<String,Integer> stationManuelList;

    public StationsManager(StationsRepository stationsRepository) {
        this.stationsRepository = stationsRepository;
        this.stationManuelList = new HashMap<>();
        this.stations = new ArrayList<>();
        init();
    }

    private void init() {
        stations = stationsRepository.getAllStations();
        stations.forEach(ownStation -> {
            if (!ownStation.getName().equalsIgnoreCase(makeStringGreatAgain(ownStation.getName())) ||
                    !ownStation.getNameWithPlace().equalsIgnoreCase(makeStringGreatAgain(ownStation.getNameWithPlace())))
            {
                ownStation.updateNames(makeStringGreatAgain(ownStation.getName()), makeStringGreatAgain(ownStation.getNameWithPlace()), stationsRepository);
            }
        });
        stationManuelList = stationsRepository.getAllStationsManuell();
    }

    public void add(XMLSTOPFINDER xmlstopfinder) {
        if (xmlstopfinder.getStopFinder().getItdOdvAssignedStops() == null) {
            if (xmlstopfinder.getStopFinder().getPoints() == null)
                return;
            for (Point point : xmlstopfinder.getStopFinder().getPoints()) {
                if (point.getAnyType().equalsIgnoreCase("stop")) {
                    if (stations.stream().anyMatch(ownStation -> ownStation.getStopID() == point.getRef().getId()))
                        continue;
                    XSLTDM xsltdm = new XSLTDepartureMonitorRequest(point.getRef().getId()+"").request();
                    add(xsltdm);
                }
            }
            return;
        }
        for (ItdOdvAssignedStop itdOdvAssignedStop : xmlstopfinder.getStopFinder().getItdOdvAssignedStops()) {
            Optional<OwnStation> ownStation = stations.stream().filter(station -> station.getStopID() == itdOdvAssignedStop.getStopID()).findAny();
            if (ownStation.isEmpty()) {
                OwnStation newOwnStation = new OwnStation(0,
                        itdOdvAssignedStop.getStopID(),
                        itdOdvAssignedStop.getGid(),
                        itdOdvAssignedStop.getX(),
                        itdOdvAssignedStop.getY(),
                        itdOdvAssignedStop.getPlace(),
                        makeStringGreatAgain(itdOdvAssignedStop.getName()),
                        makeStringGreatAgain(itdOdvAssignedStop.getNameWithPlace()));
                newOwnStation.writeToDatabase(stationsRepository);
                stations.add(newOwnStation);
            }
        }
    }

    public void add(XSLTDM xsltdm) {
        if (xsltdm == null)
            return;
        ItdOdvAssignedStops odvAssignedStops = xsltdm.getDm().getItdOdvAssignedStops();
        if (odvAssignedStops == null)
            return;
        int stopID = odvAssignedStops.getStopID();
        Optional<OwnStation> ownStation = stations.stream().filter(station -> station.getStopID() == stopID).findAny();
        if (ownStation.isEmpty()) {
            OwnStation newOwnStation = new OwnStation(0,stopID,
                    odvAssignedStops.getGid(),
                    odvAssignedStops.getX(),
                    odvAssignedStops.getY(),
                    odvAssignedStops.getPlace(),
                    makeStringGreatAgain(odvAssignedStops.getName()),
                    makeStringGreatAgain(odvAssignedStops.getNameWithPlace()));
            newOwnStation.writeToDatabase(stationsRepository);
            stations.add(newOwnStation);
        } else {
            ownStation.get().updateXY(odvAssignedStops.getX(),odvAssignedStops.getY(),stationsRepository);
        }
    }

    public OwnStation getStationByName(String name) {
        if (stationManuelList.containsKey(name))
            return getStationByStopID(stationManuelList.get(name));
        String tempName = makeStringGreatAgain(name).replace(" ","");
        for (OwnStation ownStation : stations) {
            if (tempName.equalsIgnoreCase(ownStation.getNameWithPlace().replace(" ",""))) {
                return ownStation;
            }
        }
        XMLSTOPFINDER xmlstopfinder = new XMLStopFinderRequest(makeStringGreatAgain(name)).request();
        add(xmlstopfinder);
        for (OwnStation ownStation : stations) {
            if (tempName.equalsIgnoreCase(ownStation.getNameWithPlace().replace(" ",""))) {
                return ownStation;
            }
        }
        return null;
    }

    public OwnStation getStationByStopID(int id) {
        if (stations.stream().noneMatch(station -> station.getStopID() == id)) {
            XSLTDM xsltdm = new XSLTDepartureMonitorRequest(id+"").request();
            add(xsltdm);
        }
        return stations.stream().filter(station -> station.getStopID() == id).findAny().get();
    }

    public OwnStation getStationByStopID(String id) {
        return getStationByStopID(Integer.parseInt(id));
    }

    public OwnStation getStationByOwnID(int id) {
        return stations.stream().filter(ownStation -> ownStation.getOwnStopID() == id).findAny().get();
    }

    private String makeStringGreatAgain(String name) {
        return name.replace("Hbf","Hauptbahnhof").replace("/ZOB","").replace("str.","straße");
    }

}
