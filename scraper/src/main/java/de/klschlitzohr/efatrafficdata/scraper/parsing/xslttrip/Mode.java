package de.klschlitzohr.efatrafficdata.scraper.parsing.xslttrip;

import lombok.Getter;

/**
 * Created on 01.10.2021
 *
 * @author KlSchlitzohr
 */
@Getter
public class Mode {

    private String name;
    private String number;
    private String symbol;
    private String product;
    private String productId;
    private String type;
    private String code;
    private String destination;
    private String destID;
    private String desc;
    private String timetablePeriod;
    private String realtime;
    Diva diva;

}
