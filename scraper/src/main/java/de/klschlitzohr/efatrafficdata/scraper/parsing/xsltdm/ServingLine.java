package de.klschlitzohr.efatrafficdata.scraper.parsing.xsltdm;

import lombok.Getter;

/**
 * Created on 24.09.2021
 *
 * @author KlSchlitzohr
 */
@Getter
public class ServingLine {

    private int key;
    private String code;
    private String number;
    private String symbol;
    private String motType;
    private String mtSubcode;
    private String realtime;
    private String direction;
    private String directionFrom;
    private String trainType;
    private String trainName;
    private String trainNum;
    private String name;
    private int delay;
    private LiErgRiProj liErgRiProj;
    private int destID;
    private String stateless;

}
