package de.klschlitzohr.efatrafficdata.scraper.parsing.xsltdm;

import lombok.Getter;

import java.util.List;

/**
 * Created on 24.09.2021
 *
 * @author KlSchlitzohr
 */
@Getter
public class PtOption {

    private String active;
    private String maxChanges;
    private String maxTime;
    private String maxWait;
    private String routeType;
    private String changeSpeed;
    private String lineRestriction;
    private String useProxFootSearch;
    private String useProxFootSearchOrigin;
    private String useProxFootSearchDestination;
    private String bike;
    private String plane;
    private String noCrowded;
    private String noSolidStairs;
    private String noEscalators;
    private String noElevators;
    private String lowPlatformVhcl;
    private String wheelchair;
    private String needElevatedPlt;
    private String assistance;
    private String SOSAvail;
    private String noLonelyTransfer;
    private String illumTransfer;
    private String overgroundTransfer;
    private String noInsecurePlaces;
    private String privateTransport;
    private List<Object> excludedMeans;
    private String activeImp;
    private String activeCom;
    private String activeSec;

}
