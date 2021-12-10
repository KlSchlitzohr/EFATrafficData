package de.klschlitzohr.efatrafficdata.scraper.parsing.xmlstopfinder;

import lombok.Getter;

import java.util.List;

/**
 * Created on 09.10.2021
 *
 * @author KlSchlitzohr
 */
@Getter
public class StopFinder {

    private Input input;
    private List<Message> message;
    private List<Point> points;
    private List<ItdOdvAssignedStop> itdOdvAssignedStops;

}
