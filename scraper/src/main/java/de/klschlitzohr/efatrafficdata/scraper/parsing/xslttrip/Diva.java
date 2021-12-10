package de.klschlitzohr.efatrafficdata.scraper.parsing.xslttrip;

import lombok.Getter;

import java.util.List;

/**
 * Created on 01.10.2021
 *
 * @author KlSchlitzohr
 */
@Getter
public class Diva {

    private String branch;
    private String line;
    private String supplement;
    private String dir;
    private String project;
    private String network;
    private String stateless;
    private int tripCode;
    private String operator;
    private String opPublicCode;
    private String opCode;
    private String isTTB;
    private String isSTT;
    private String isROP;
    List<Object> attrs;

}
