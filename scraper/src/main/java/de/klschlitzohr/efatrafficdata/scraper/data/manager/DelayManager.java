package de.klschlitzohr.efatrafficdata.scraper.data.manager;

import de.klschlitzohr.efatrafficdata.api.data.model.OwnDelay;
import de.klschlitzohr.efatrafficdata.api.data.model.OwnLine;
import de.klschlitzohr.efatrafficdata.api.database.repositories.LineRepository;
import de.klschlitzohr.efatrafficdata.scraper.parsing.xsltdm.Departure;
import de.klschlitzohr.efatrafficdata.scraper.parsing.xsltdm.XSLTDM;
import de.klschlitzohr.efatrafficdata.scraper.request.XSLTDepartureMonitorRequest;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
public class DelayManager {

    private final LineManager lineManager;
    private final LineRepository lineRepository;
    private final List<OwnDelay> delays;

    public DelayManager(LineRepository lineRepository, LineManager lineManager) {
        this.lineRepository = lineRepository;
        this.lineManager = lineManager;
        this.delays = Collections.synchronizedList(new ArrayList<>());
    }

    public void saveDelays(XSLTDepartureMonitorRequest xsltDepartureMonitorRequest) {
        cleanDelays();
        XSLTDM xsltdm = xsltDepartureMonitorRequest.getXsltdm();
        if (xsltdm.getDepartureList() != null) {
            for (Departure departure : xsltdm.getDepartureList()) {
                if (departure.getServingLine() == null)
                    continue;
                if (departure.getServingLine().getStateless() == null)
                    continue;
                if (departure.getServingLine().getKey() == 0)
                    continue;
                if (departure.getDateTime() == null)
                    continue;
                OwnLine ownLine = lineManager.getLineByStatless(departure.getServingLine().getStateless());
                if (ownLine != null) {
                    int delay = departure.getRealDateTime() != null ? departure.getServingLine().getDelay() : -999999;
                    OwnDelay ownDelay = new OwnDelay(ownLine.getOwnLineID(),
                            departure.getServingLine().getKey(),
                            xsltDepartureMonitorRequest.getLocalDateTime(),
                            delay);
                    LocalDateTime dateTime = LocalDateTime.of(departure.getDateTime().getYear(),departure.getDateTime().getMonth(),departure.getDateTime().getDay(),departure.getDateTime().getHour(),departure.getDateTime().getMinute());
                    if (ChronoUnit.MINUTES.between(dateTime,LocalDateTime.now()) > 180)
                        continue;
                    if (delays.stream().anyMatch(deplay -> ownDelay.getLineID() == deplay.getLineID() && ownDelay.getKey() == deplay.getKey())) {
                        if (delays.stream().anyMatch(deplay -> ownDelay.getLineID() == deplay.getLineID() && ownDelay.getKey() == deplay.getKey() && deplay.getDelay() != ownDelay.getDelay())) {
                            OwnDelay ownDelayInList = delays.stream().filter(deplay -> ownDelay.getLineID() == deplay.getLineID() && ownDelay.getKey() == deplay.getKey()).findAny().get();
                            if (ownDelayInList.getDelay() == -999999)
                                continue;
                            if (ownDelay.getDelay() > ownDelayInList.getDelay()) {
                                ownDelayInList.updateDelay(lineRepository,ownDelay.getDelay());
                            }
                        }
                        continue;
                    }
                    delays.add(ownDelay);
                    if (ownDelay.getDelay() == -999999)
                        continue;
                    ownDelay.writeToDatabase(lineRepository);
                }
            }
        }
    }

    private void cleanDelays() {
        synchronized (delays) {
            delays.removeIf(delay -> ChronoUnit.SECONDS.between(delay.getLocalDateTime(), LocalDateTime.now()) >= 60);
        }
    }

}
