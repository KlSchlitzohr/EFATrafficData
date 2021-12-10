package de.klschlitzohr.efatrafficdata.scraper.parsing.xslttrip;

import lombok.Getter;

/**
 * Created on 24.09.2021
 *
 * @author KlSchlitzohr
 */
@Getter
public class ItdOdvAssignedStops {

    private String stopID;
    private String name;
    private String x;
    private String y;
    private String mapName;
    private String value;
    private String place;
    private String nameWithPlace;
    private String distanceTime;
    private String isTransferStop;
    private String vm;
    private String gid;

}
