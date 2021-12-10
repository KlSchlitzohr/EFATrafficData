package de.klschlitzohr.efatrafficdata.scraper.parsing.xslttrip;

import lombok.Getter;

import java.util.List;

/**
 * Created on 01.10.2021
 *
 * @author KlSchlitzohr
 */
@Getter
public class XSLTTRIP {

    List<Parameter> parameters;
    List<Object> itdMessageList;
    //Origin origin;
    //Destination destination;
    Via via;
    DateTime dateTime;
    List<Object> dateRange;
    Option option;
    List<Trip> trips;

}
