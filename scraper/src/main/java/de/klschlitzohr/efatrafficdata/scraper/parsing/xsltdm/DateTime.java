package de.klschlitzohr.efatrafficdata.scraper.parsing.xsltdm;

import lombok.Getter;

/**
 * Created on 24.09.2021
 *
 * @author KlSchlitzohr
 */
@Getter
public class DateTime {

    private String deparr;
    private String ttpFrom;
    private String ttpTo;
    private int year;
    private int month;
    private int day;
    private int hour;
    private int minute;
    private int weekday;

}
