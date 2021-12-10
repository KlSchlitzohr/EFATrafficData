package de.klschlitzohr.efatrafficdata.scraper.parsing.xsltdm;

import lombok.Getter;

import java.util.List;

/**
 * Created on 24.09.2021
 *
 * @author KlSchlitzohr
 */
@Getter
public class Departure {

    private int stopID;
    private String x;
    private String y;
    private String mapName;
    private String area;
    private String platform;
    private String platformName;
    private String stopName;
    private String nameWO;
    private String pointType;
    private String countdown;
    private DateTime dateTime;
    private RealDateTime realDateTime;
    private ServingLine servingLine;
    private Operator operator;
    private List<Object> attrs;

}
