package de.klschlitzohr.efatrafficdata.scraper.parsing.xslttrip;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 01.10.2021
 *
 * @author KlSchlitzohr
 */
@Getter
public class TariffZone {

    private String net;
    private String toPR;
    private String fromPR;
    private String neutralZone;
    List<Object> zones;

}
