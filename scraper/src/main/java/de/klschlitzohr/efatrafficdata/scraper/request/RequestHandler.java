package de.klschlitzohr.efatrafficdata.scraper.request;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created on 24.10.2021
 *
 * @author KlSchlitzohr
 */
@Getter
public class RequestHandler {

    private final List<XMLStopFinderRequest> xmlStopFinderRequests;
    private final List<XSLTTripRequest> xsltTripRequests;
    private final List<XSLTDepartureMonitorRequest> xsltDepartureMonitorRequests;

    public RequestHandler() {
        xmlStopFinderRequests = Collections.synchronizedList(new ArrayList<>());
        xsltTripRequests = Collections.synchronizedList(new ArrayList<>());
        xsltDepartureMonitorRequests = Collections.synchronizedList(new ArrayList<>());
    }

    public void checkTime() {
        synchronized (xmlStopFinderRequests) {
            xmlStopFinderRequests.removeIf(obj -> obj == null || obj.getTime() + 180000 < System.currentTimeMillis());
        }
        synchronized (xsltTripRequests) {
            xsltTripRequests.removeIf(obj -> obj == null || obj.getTime() + 180000 < System.currentTimeMillis());
        }
        synchronized (xsltDepartureMonitorRequests) {
            xsltDepartureMonitorRequests.removeIf(obj -> obj == null || obj.getTime() + 60000 < System.currentTimeMillis());
        }
    }

    public void addStopFinder(XMLStopFinderRequest xmlStopFinderRequest) {
        xmlStopFinderRequests.add(xmlStopFinderRequest);
    }

    public void addTrip(XSLTTripRequest xsltTripRequest) {
        xsltTripRequests.add(xsltTripRequest);
    }

    public void addDmR(XSLTDepartureMonitorRequest xsltDepartureMonitorRequest) {
        xsltDepartureMonitorRequests.add(xsltDepartureMonitorRequest);
    }

    public int getRequestCountInLastMinute() {
        return (xmlStopFinderRequests.size() / 3) + (xsltTripRequests.size() / 3) + xsltDepartureMonitorRequests.size();
    }
}
