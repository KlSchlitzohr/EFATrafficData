package de.klschlitzohr.efatrafficdata.scraper.parsing.xsltdm;

import lombok.Getter;

/**
 * Created on 24.09.2021
 *
 * @author KlSchlitzohr
 */
@Getter
public class Point {

    private String usage;
    private String type;
    private String name;
    private String stateless;
    private String anyType;
    private String sort;
    private String quality;
    private String best;
    private String object;
    private Ref ref;
    //private String infos; //Commented hier sind umleitungs infos

}
