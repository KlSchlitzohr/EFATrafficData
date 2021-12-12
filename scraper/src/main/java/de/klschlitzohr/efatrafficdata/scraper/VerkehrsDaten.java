package de.klschlitzohr.efatrafficdata.scraper;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.klschlitzohr.efatrafficdata.api.configuration.VerkehrsDatenConfiguration;
import de.klschlitzohr.efatrafficdata.api.configuration.VerkehrsDatenConfigurationLoader;
import de.klschlitzohr.efatrafficdata.api.data.model.OwnLine;
import de.klschlitzohr.efatrafficdata.api.data.model.OwnLineStop;
import de.klschlitzohr.efatrafficdata.api.data.model.OwnStation;
import de.klschlitzohr.efatrafficdata.api.database.DatabaseManager;
import de.klschlitzohr.efatrafficdata.api.database.repositories.LineRepository;
import de.klschlitzohr.efatrafficdata.api.database.repositories.SqlLineRepository;
import de.klschlitzohr.efatrafficdata.api.database.repositories.SqlStationsRepository;
import de.klschlitzohr.efatrafficdata.api.database.repositories.StationsRepository;
import de.klschlitzohr.efatrafficdata.scraper.commands.CommandScanner;
import de.klschlitzohr.efatrafficdata.scraper.data.manager.DelayManager;
import de.klschlitzohr.efatrafficdata.scraper.data.manager.LineManager;
import de.klschlitzohr.efatrafficdata.scraper.data.manager.StationsManager;
import de.klschlitzohr.efatrafficdata.scraper.parsing.xmlstopfinder.StopFinder;
import de.klschlitzohr.efatrafficdata.scraper.parsing.xmlstopfinder.deserializers.StopFinderDeserializer;
import de.klschlitzohr.efatrafficdata.scraper.parsing.xsltdm.Departure;
import de.klschlitzohr.efatrafficdata.scraper.parsing.xsltdm.XSLTDM;
import de.klschlitzohr.efatrafficdata.scraper.parsing.xsltdm.deserializers.XSLTDMDeserializer;
import de.klschlitzohr.efatrafficdata.scraper.parsing.xslttrip.XSLTTRIP;
import de.klschlitzohr.efatrafficdata.scraper.parsing.xslttrip.deserializers.XSLTTripDEserializer;
import de.klschlitzohr.efatrafficdata.scraper.request.RequestHandler;
import de.klschlitzohr.efatrafficdata.scraper.request.XSLTDepartureMonitorRequest;
import de.klschlitzohr.efatrafficdata.scraper.request.XSLTTripRequest;
import de.klschlitzohr.efatrafficdata.scraper.utils.TempStation;
import io.sentry.Sentry;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created on 24.09.2021
 *
 * @author KlSchlitzohr
 */
@Getter
@Log4j2
public class VerkehrsDaten {

    private VerkehrsDatenConfiguration configuration;
    private RequestHandler requestHandler;
    private LineRepository lineRepository;
    private StationsRepository stationsRepository;
    private DatabaseManager databaseManager;
    private StationsManager stationsManager;
    private DelayManager delayManager;
    private LineManager lineManager;
    private Gson gson;

    @Setter
    private boolean stop = false;

    public VerkehrsDaten() {

    }

    public void init() {
        configuration = VerkehrsDatenConfigurationLoader.loadConfiguration("app.config");
        initSentry();
        databaseManager = new DatabaseManager(configuration.getDatabase());
        requestHandler = new RequestHandler();
        lineRepository = new SqlLineRepository(databaseManager);
        stationsRepository = new SqlStationsRepository(databaseManager);
        stationsManager = new StationsManager(stationsRepository);
        lineManager = new LineManager(stationsManager, lineRepository);
        delayManager = new DelayManager(lineRepository,lineManager);
        gson = new GsonBuilder()
                .registerTypeAdapter(StopFinder.class, new StopFinderDeserializer())
                .registerTypeAdapter(XSLTDM.class, new XSLTDMDeserializer())
                .registerTypeAdapter(XSLTTRIP.class, new XSLTTripDEserializer())
                .create();
    }

    public void test() {
        XSLTDM xsltdm = new XSLTDepartureMonitorRequest("6005461").request();
        stationsManager.add(xsltdm);
    }

    public void loop() {
        new CommandScanner(this);
        long runtime = System.currentTimeMillis();
        long lastrun;
        while (runtime + 1000*60*60*8 > System.currentTimeMillis()) {
            if (stop)
                break;
            log.debug("Weiter");
            lastrun = System.currentTimeMillis();
            try {
                makeOneRound();
                requestHandler.getXsltDepartureMonitorRequests().clear();
            } catch (Exception e) {
                log.error(e);
            }
            while ((lastrun + 1000*60) > System.currentTimeMillis()) {
                log.debug("Waiting... " + System.currentTimeMillis() + " " + (lastrun + 1000*60));
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    log.error(e);
                }
                if (stop)
                    break;
            }
            log.info("LOOP Baby");
        }
        System.exit(0);
    }

    public void makeOneRound() {

        // Prioritize lines
        long start = System.currentTimeMillis();
        List<OwnStation> stationsInRegion = getStationsInRegion();
        ArrayList<TempStation> stations = new ArrayList<>();
        for (OwnLine ownLine : lineManager.getLines()) {
            OwnLineStop lastLineStop = ownLine.getOwnLineStops().stream().max(Comparator.comparingInt(OwnLineStop::getDepartureDelay)).get();
            if (stationsInRegion.stream().noneMatch(station -> station.getOwnStopID() == lastLineStop.getStopID()))
                continue;
            if (stations.stream().noneMatch(tempStation1 -> tempStation1.getStationID() == lastLineStop.getStopID()))
                stations.add(new TempStation(lastLineStop.getStopID(),10));
            else
                stations.stream().filter(tempStation1 -> tempStation1.getStationID() == lastLineStop.getStopID()).findFirst().get().better(10);
        }

        for (OwnLine ownLine : lineManager.getLines()) {
            for (OwnLineStop ownLineStop : ownLine.getOwnLineStops()) {
                if (stationsInRegion.stream().noneMatch(station -> station.getOwnStopID() == ownLineStop.getStopID()))
                    continue;
                if (stations.stream().noneMatch(tempStation1 -> tempStation1.getStationID() == ownLineStop.getStopID()))
                    stations.add(new TempStation(ownLineStop.getStopID(),1));
                else
                    stations.stream().filter(tempStation1 -> tempStation1.getStationID() == ownLineStop.getStopID()).findFirst().get().better(1);
            }
        }

        // Sort
        stations.sort(Comparator.comparingInt(TempStation::getCount));
        Collections.reverse(stations);

        // Max lines = 400
        while (stations.size() > 400) {
            stations.remove(stations.size()-1);
        }

        // Remove Double Stations
        TempStation[] stationsClone = stations.toArray(new TempStation[0]);
        double time = System.currentTimeMillis();
        log.debug("Start Stations: " + stations.size());

        for (TempStation tempStation : stationsClone) {
            OwnStation ownStation = stationsManager.getStationByOwnID(tempStation.getStationID());
            getLineManager().getLines().forEach(ownLine -> ownLine.getOwnLineStops().forEach(ownLineStop -> {
                if (ownLineStop.getStopID() == ownStation.getOwnStopID()) {
                    tempStation.addLine(ownLine);
                }
            }));
        }
        log.debug("Stations Anders: " + stations.size());

        for (TempStation tempStation : stationsClone) {
            for (TempStation tempStation1 : stationsClone) {
                if (tempStation.getStationID() == tempStation1.getStationID()) {
                    continue;
                }
                List<OwnLine> ownStationsLines = tempStation.getLineList();
                List<OwnLine> ownStationsLines1 = tempStation1.getLineList();

                boolean ownLine1ContainsAll2 = true;
                for (OwnLine ownLine : ownStationsLines) {
                    if (ownStationsLines1.stream().noneMatch(ownLine1 -> ownLine1.getOwnLineID() == ownLine.getOwnLineID())) {
                        ownLine1ContainsAll2 = false;
                        break;
                    }
                }

                boolean ownLine2ContainsAll1 = true;
                for (OwnLine ownLine : ownStationsLines1) {
                   if (ownStationsLines.stream().noneMatch(ownLine1 -> ownLine1.getOwnLineID() == ownLine.getOwnLineID())) {
                       ownLine2ContainsAll1 = false;
                       break;
                   }
                }

                if (ownLine1ContainsAll2 && ownLine2ContainsAll1) {
                    if (ownStationsLines.size() >= ownStationsLines1.size()) {
                        stations.remove(tempStation1);
                    } else {
                        stations.remove(tempStation);
                    }
                } else if (ownLine1ContainsAll2) {
                    stations.remove(tempStation1);
                } else if (ownLine2ContainsAll1) {
                    stations.remove(tempStation);
                }
            }
        }
        log.debug("Stations: " + stations.size());

        // Remove Stations if they are already conatined in the list
        log.debug("Stations BEVOR 2 CLEAN: " + stations.size());
        List<OwnLine> ownLines = new ArrayList<>();
        for (TempStation tempStation : stationsClone) {
            boolean containsAll = true;
            for (OwnLine ownLine : tempStation.getLineList()) {
                if (!ownLines.contains(ownLine)) {
                    containsAll = false;
                    ownLines.add(ownLine);
                }
            }
            if (!containsAll) {
                stations.remove(tempStation);
            }
        }
        log.debug("Stations AFTER 2 CLEAN: " + stations.size());

        //Max lines = 200
        while (stations.size() > 200) {
            stations.remove(stations.size()-1);
        }

        // make requests
        for (TempStation tempStation : stations) {
            log.debug(stationsManager.getStationByOwnID(tempStation.getStationID()).getNameWithPlace() + " " + tempStation.getCount());
            try {
                makeDepartureRequest(stationsManager.getStationByOwnID(tempStation.getStationID()).getStopID() + "");
            } catch (Exception e) {
                log.error(e);
            }
            requestHandler.checkTime();
            if (requestHandler.getRequestCountInLastMinute() > 140 || (start + 60*1000) < System.currentTimeMillis()) {
                break;
            }
            if (stop)
                break;
        }
    }

    public List<OwnStation> getStationsInRegion() {
        return stationsManager.getStations().stream().filter(station -> station.getX() > 7.4 && station.getX() < 8.7)
                .filter(station -> station.getY() < 49.700 && station.getY() > 49.2).collect(Collectors.toList());
    }

    public void makeDepartureRequest(String id) {
        log.debug("Start with id " + id);
        XSLTDM xsltDepartureMonitorRequest = new XSLTDepartureMonitorRequest(id).request();
        stationsManager.add(xsltDepartureMonitorRequest);
        for (Departure departure : xsltDepartureMonitorRequest.getDepartureList()) {
            if (departure == null)
                continue;
            if (departure.getServingLine() == null)
                continue;
            if (departure.getServingLine().getDestID() == -1)
                continue;
            if (stationsManager.getStations().stream().noneMatch(ownStation -> departure.getServingLine().getDestID() == ownStation.getStopID())) {
                XSLTDM xsltDepartureMonitorRequest2 = new XSLTDepartureMonitorRequest(departure.getServingLine().getDestID()+"").request();
                stationsManager.add(xsltDepartureMonitorRequest2);
            }
        }
        for (Departure departure : xsltDepartureMonitorRequest.getDepartureList()) {
            if (departure == null)
                continue;
            if (departure.getServingLine() == null)
                continue;
            OwnStation ownStationFrom = stationsManager.getStationByName(departure.getServingLine().getDirectionFrom());
            if (ownStationFrom == null)
                continue;
            String directionFromID = ownStationFrom.getStopID()+"";
            OwnStation ownStation;
            if (departure.getServingLine().getDestID() == 0 || departure.getServingLine().getDestID() == -1) {
                ownStation = stationsManager.getStationByName(departure.getServingLine().getDirection());
            } else {
                ownStation = stationsManager.getStationByStopID(departure.getServingLine().getDestID());
            }
            if (ownStation == null)
                continue;
            if (Integer.parseInt(departure.getCountdown()) > 60)
                continue;
            if ((stationsManager.getStationByName(departure.getServingLine().getDirectionFrom()).getStopID()+"").equalsIgnoreCase(directionFromID)) {
                if (ownStationFrom.distance(ownStation) < 2) {
                    if (!directionFromID.equalsIgnoreCase(ownStation.getStopID()+"")) {
                        if (lineManager.getLines().stream().noneMatch(line -> line.getStateless().equalsIgnoreCase(departure.getServingLine().getStateless()))) {
                            XSLTTRIP xslttrip = new XSLTTripRequest(directionFromID, ownStation.getStopID() + "").request();
                            lineManager.add(xslttrip);
                        }
                    }
                }
            }
        }
        lineManager.addLineStarts(xsltDepartureMonitorRequest);
        delayManager.saveDelays(xsltDepartureMonitorRequest.getXsltDepartureMonitorRequest());
    }

    private void initSentry() {
        // Enable Sentry only in Prod. Prod did not use Windows so this hack will work.
        boolean enableSentry = !System.getProperty("os.name").contains("Windows");
        if (enableSentry) {
            log.info("Init Sentry...");
            Sentry.init(options -> {
                options.setDsn(configuration.getSentryDsn());
                // Set traces_sample_rate to 1.0 to capture 100% of transactions for performance monitoring.
                // We recommend adjusting this value in production.
                options.setTracesSampleRate(1.0);
                options.setSendDefaultPii(true);
            });
        }
    }
}
