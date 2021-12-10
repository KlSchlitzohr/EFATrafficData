package de.klschlitzohr.efatrafficdata.scraper.parsing.xmlstopfinder;

import lombok.Getter;

/**
 * Created on 09.10.2021
 *
 * @author KlSchlitzohr
 */
@Getter
public class Point {

    private Ref ref;
    private String usage;
    private String type;
    private String name;
    private String stateless;
    private String anyType;
    private String sort;
    private String quality;
    private String best;
    private String object;
    private String mainLoc;
    private String modes;

}
