package de.klschlitzohr.efatrafficdata.scraper.parsing.xsltdm;

import lombok.Getter;

import java.util.List;

/**
 * Created on 24.09.2021
 *
 * @author KlSchlitzohr
 */
@Getter
public class ServingLines {

    private String trainInfo;
    private String selected;
    private List<Object> lines;

}
