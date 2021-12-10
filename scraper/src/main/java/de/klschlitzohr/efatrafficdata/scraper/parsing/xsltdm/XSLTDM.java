package de.klschlitzohr.efatrafficdata.scraper.parsing.xsltdm;

import de.klschlitzohr.efatrafficdata.scraper.request.XSLTDepartureMonitorRequest;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 24.09.2021
 *
 * @author KlSchlitzohr
 */
@Getter
public class XSLTDM {

    ArrayList<Parameter> parameters;
    Dm dm;
    Arr arr;
    DateTime DateTime;
    List<Object> dateRange;
    Option option;
    //ServingLines servingLines;
    List<Departure> departureList;

    @Setter
    private XSLTDepartureMonitorRequest xsltDepartureMonitorRequest;

}
