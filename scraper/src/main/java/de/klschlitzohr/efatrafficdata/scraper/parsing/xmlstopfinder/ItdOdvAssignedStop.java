package de.klschlitzohr.efatrafficdata.scraper.parsing.xmlstopfinder;

import lombok.Getter;

/**
 * Created on 14.10.2021
 *
 * @author KlSchlitzohr
 */
@Getter
public class ItdOdvAssignedStop {

    private int stopID;
    private String name;
    private double x;
    private double y;
    private String mapName;
    private String value;
    private String place;
    private String nameWithPlace;
    private String distanceTime;
    private String isTransferStop;
    private String vm;
    private String gid;
}
