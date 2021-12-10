package de.klschlitzohr.efatrafficdata.scraper.parsing.xslttrip;

import lombok.Getter;

import java.util.List;

/**
 * Created on 01.10.2021
 *
 * @author KlSchlitzohr
 */
@Getter
public class Leg {

    private String timeMinute;
    List<Point> points;
    Controlled controlled;
    Mode mode;
    //List<Hint> hints;
    List<Stop> stopSeq;
   // ArrayList<Info> infos;
    private String format;
    private String path;

}
