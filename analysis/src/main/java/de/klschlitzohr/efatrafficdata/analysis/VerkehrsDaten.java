package de.klschlitzohr.efatrafficdata.analysis;

import de.klschlitzohr.efatrafficdata.analysis.data.manager.LineManager;
import de.klschlitzohr.efatrafficdata.analysis.data.manager.StationsManager;
import de.klschlitzohr.efatrafficdata.analysis.utils.ReadingGermanyData;
import de.klschlitzohr.efatrafficdata.analysis.utils.TempLine;
import de.klschlitzohr.efatrafficdata.api.configuration.VerkehrsDatenConfiguration;
import de.klschlitzohr.efatrafficdata.api.configuration.VerkehrsDatenConfigurationLoader;
import de.klschlitzohr.efatrafficdata.api.data.model.OwnLine;
import de.klschlitzohr.efatrafficdata.api.data.model.OwnStation;
import de.klschlitzohr.efatrafficdata.api.database.DatabaseManager;
import de.klschlitzohr.efatrafficdata.api.database.repositories.LineRepository;
import de.klschlitzohr.efatrafficdata.api.database.repositories.SqlLineRepository;
import de.klschlitzohr.efatrafficdata.api.database.repositories.SqlStationsRepository;
import de.klschlitzohr.efatrafficdata.api.database.repositories.StationsRepository;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.sql.ResultSet;
import java.time.LocalTime;
import java.util.ArrayList;

/**
 * Created on 24.09.2021
 *
 * @author KlSchlitzohr
 */
@Getter
@Log4j2
public class VerkehrsDaten {

    private final VerkehrsDatenConfiguration configuration;
    private final LineRepository lineRepository;
    private final StationsRepository stationsRepository;
    private final DatabaseManager databaseManager;
    private final StationsManager stationsManager;
    //private final DelayManager delayManager;
    private final LineManager lineManager;


    public VerkehrsDaten() {
        configuration = VerkehrsDatenConfigurationLoader.loadConfiguration("analysis.config");
        databaseManager = new DatabaseManager(configuration.getDatabase());
        lineRepository = new SqlLineRepository(databaseManager);
        stationsRepository = new SqlStationsRepository(databaseManager);
        stationsManager = new StationsManager(stationsRepository);
        lineManager = new LineManager(lineRepository,stationsManager);
        //delayManager = new DelayManager(databaseManager,lineManager);
    }

    public ArrayList<TempLine> getLiveLines() {
        ArrayList<TempLine> ownLines = new ArrayList<>();
        ResultSet resultSet = databaseManager.getResult("SELECT lineID, SUM(delay) AS delay, COUNT(lineID) AS count, requestTime FROM `lineDelays` WHERE requestTime > '2021-12-17' GROUP By lineID ");
        try {
            while (resultSet.next()) {
               int count = resultSet.getInt("count");
               if (count > 100)
               ownLines.add(new TempLine(lineManager.getOwnLinebyOwnId(resultSet.getInt("lineID")),resultSet.getInt("delay"),count));
            }
        } catch (Exception e) {
            log.error(e);
        }
        /*int allDelays = ownLines.stream().mapToInt(TempLine::getDelay).sum();
        int averageDelay = allDelays / ownLines.size();
        int color = allDelays * 255 / averageDelay;*/
        int allCount = ownLines.stream().mapToInt(TempLine::getCount).sum();
        int averageCount = allCount / ownLines.size();

        ownLines.forEach(tempLine -> tempLine.setColor(tempLine.getCount() * 255 / averageCount));
        return ownLines;
    }

    public ArrayList<OwnStation> getLiveStations(ArrayList<TempLine> liveLines) {
        ArrayList<OwnStation> ownStations = new ArrayList<>();
        for (TempLine tempLine : liveLines) {
            OwnLine ownLine = tempLine.getOwnLine();
            ownLine.getOwnLineStops().forEach(ownLineStop ->
               ownStations.add(stationsManager.getStationByOwnID(ownLineStop.getStopID())));
        }
        return ownStations;
    }

    public void test() {
        log.info("Starting..");
        ReadingGermanyData readingGermanyData = new ReadingGermanyData();
        log.info("Get Live Lines");
        ArrayList<TempLine> liveLines = getLiveLines();
        log.info("Get Live Stations");
        var liveStations = getLiveStations(liveLines);
        log.info("Turn things up...");
        readingGermanyData.getGrid().setOwnStationList(liveStations);
        readingGermanyData.getGrid().setOwnLinesList(liveLines);
        readingGermanyData.getGrid().updateImage();
        log.info("Done");
    }

    private LocalTime getLocalTime(LocalTime localTime) {
        return localTime.minusSeconds(localTime.getSecond()).minusMinutes(localTime.getMinute() % 15);
    }

}
