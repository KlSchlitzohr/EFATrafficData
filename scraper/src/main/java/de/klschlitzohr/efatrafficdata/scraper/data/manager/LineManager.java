package de.klschlitzohr.efatrafficdata.scraper.data.manager;

import de.klschlitzohr.efatrafficdata.api.data.model.OwnLine;
import de.klschlitzohr.efatrafficdata.api.data.model.OwnLineStart;
import de.klschlitzohr.efatrafficdata.api.data.model.OwnLineStop;
import de.klschlitzohr.efatrafficdata.api.data.model.OwnStation;
import de.klschlitzohr.efatrafficdata.api.database.repositories.LineRepository;
import de.klschlitzohr.efatrafficdata.scraper.parsing.xsltdm.DateTime;
import de.klschlitzohr.efatrafficdata.scraper.parsing.xsltdm.Departure;
import de.klschlitzohr.efatrafficdata.scraper.parsing.xsltdm.XSLTDM;
import de.klschlitzohr.efatrafficdata.scraper.parsing.xslttrip.Leg;
import de.klschlitzohr.efatrafficdata.scraper.parsing.xslttrip.Stop;
import de.klschlitzohr.efatrafficdata.scraper.parsing.xslttrip.Trip;
import de.klschlitzohr.efatrafficdata.scraper.parsing.xslttrip.XSLTTRIP;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created on 19.10.2021
 *
 * @author KlSchlitzohr
 */
@Getter
@Log4j2
public class LineManager {

    private List<OwnLine> lines;
    private List<OwnLineStart> linesStart;
    private final StationsManager stationsManager;
    private final LineRepository lineRepository;

    public LineManager(StationsManager stationsManager, LineRepository lineRepository) {
        this.stationsManager = stationsManager;
        this.lineRepository = lineRepository;
        this.linesStart = new ArrayList<>();
        this.lines = new ArrayList<>();
        init();
    }

    private void init() {
        lines = lineRepository.getAllLinesWithStops();
        linesStart = lineRepository.getAllLineStarts();
    }

    public void addLineStarts(XSLTDM xsltdm) {
        for (Departure departure : xsltdm.getDepartureList()) {
            OwnStation directionFormStation = stationsManager.getStationByName(departure.getServingLine().getDirectionFrom());
            if (directionFormStation == null)
                continue;
            if (directionFormStation.getStopID() != departure.getStopID())
                continue;
            OwnLine ownLine = getLineByStatless(departure.getServingLine().getStateless());
            if (ownLine != null) {
                if (linesStart.stream().noneMatch(linesStart -> linesStart.getKey() == departure.getServingLine().getKey())) {
                    OwnLineStart ownLineStart = new OwnLineStart(ownLine.getOwnLineID(), departure.getServingLine().getKey(), getTimeByDateTime(departure.getDateTime()));
                    log.debug("Add LINE START "+ ownLineStart.getLineID() + " " + ownLineStart.getKey() + " " + ownLineStart.getStartTime().toString());
                    lineRepository.insertLineStart(ownLineStart);
                    linesStart.add(ownLineStart);
                }
            }
        }
    }

    public void add(XSLTTRIP xslttrip) {
        if (xslttrip.getTrips() == null)
            return;
        for (Trip trip : xslttrip.getTrips()) {
            if (trip.getLegs().size() == 1) {
                Leg leg = trip.getLegs().get(0);
                if (lines.stream().anyMatch(line -> line.getStateless().equalsIgnoreCase(leg.getMode().getDiva().getStateless())))
                    continue;
                if (leg.getStopSeq() == null) {
                    continue;
                }
                OwnLine ownLine = new OwnLine(0, leg.getMode().getDiva().getStateless(),
                        leg.getMode().getName(), leg.getMode().getNumber(), leg.getMode().getDiva().getOpPublicCode());
                ownLine.setOwnLineID(lineRepository.insertLine(ownLine));
                log.debug("Add line " + ownLine.getStateless() + " " + ownLine.getName() + " " + ownLine.getOperator());
                lines.add(ownLine);
                LocalDateTime startTime = getTime(leg.getStopSeq().get(0).getRef().getDepDateTime());
                ownLine.addStop(new OwnLineStop(ownLine.getOwnLineID(),stationsManager.getStationByStopID(
                        leg.getPoints().get(1).getRef().getId()).getOwnStopID(),
                        (int) ChronoUnit.MINUTES.between(startTime,getTimeFromStamp(leg.getPoints().get(1).getDateTime()))),
                        lineRepository);
                ownLine.setPath(leg.getPath(), lineRepository);
                for (Stop stop : leg.getStopSeq()) {
                    if (stop.getRef().getDepDateTime() == null)
                        continue;
                    LocalDateTime stopTime = getTime(stop.getRef().getDepDateTime());
                    if (stopTime != null)
                        ownLine.addStop(new OwnLineStop(ownLine.getOwnLineID(),stationsManager.getStationByStopID(
                                stop.getRef().getId()).getOwnStopID(),(int) ChronoUnit.MINUTES.between(startTime,stopTime))
                                ,lineRepository);
                }
                return;
            }
        }

    }

    private LocalTime getTimeByDateTime(DateTime dateTime) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
            return LocalTime.parse((dateTime.getHour() <= 9 ? "0"+dateTime.getHour() : dateTime.getHour())
                    + ":" + (dateTime.getMinute() <= 9 ? "0" +dateTime.getMinute() : dateTime.getMinute()), formatter);
        } catch (Exception e) {
            log.error(e);
            return null;
        }
    }

    private LocalDateTime getTimeFromStamp(de.klschlitzohr.efatrafficdata.scraper.parsing.xslttrip.DateTime dateTimeObj) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
            return LocalDateTime.parse(dateTimeObj.getDate() + " " +
                    dateTimeObj.getTime(), formatter);
        } catch (Exception e) {
            log.error(e);
            return null;
        }
    }

    public OwnLine getLineByStatless(String statless) {
        Optional<OwnLine> any = lines.stream().filter(line -> line.getStateless().equalsIgnoreCase(statless)).findAny();
        return any.orElse(null);
    }


    private LocalDateTime getTime(String timeString) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd HH:mm");
            return LocalDateTime.parse(timeString, formatter);
        } catch (Exception e) {
            log.error(e);
            return null;
        }
    }

}
