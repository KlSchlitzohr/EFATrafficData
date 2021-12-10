package de.klschlitzohr.efatrafficdata.scraper.parsing.xslttrip;

import lombok.Getter;

/**
 * Created on 24.09.2021
 *
 * @author KlSchlitzohr
 */
@Getter
public class Ref {

    private int id;
    private String area;
    private String platform;
    private String gid;
    private String areaGid;
    private String pointGid;
    private String zone;
    private String omc;
    private String placeID;
    private String place;
    private String coords;
    private String niveau;
    private String depDateTime;
    private String depDateTimeSec;
    private String arrDelay;
    private String arrValid;
    private String depDelay;
    private String depValid;

}
