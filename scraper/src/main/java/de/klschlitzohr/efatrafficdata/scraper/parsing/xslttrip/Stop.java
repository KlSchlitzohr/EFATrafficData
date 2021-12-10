package de.klschlitzohr.efatrafficdata.scraper.parsing.xslttrip;

import lombok.Getter;

/**
 * Created on 01.10.2021
 *
 * @author KlSchlitzohr
 */
@Getter
public class Stop {

    private String name;
    private String nameWO;
    private String place;
    private String nameWithPlace;
    private String omc;
    private String placeID;
    private String platformName;
    private String desc;
    Ref ref;

}
