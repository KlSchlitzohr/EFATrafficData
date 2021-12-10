package de.klschlitzohr.efatrafficdata.scraper.parsing.xslttrip;

import lombok.Getter;

import java.util.List;

/**
 * Created on 01.10.2021
 *
 * @author KlSchlitzohr
 */
@Getter
public class Trip {

    private String distance;
    private String duration;
    private String interchange;
    private String desc;
    private String optValue;
    List<Leg> legs;
    ItdFare itdFare;
    List<Object> attrs;

}
