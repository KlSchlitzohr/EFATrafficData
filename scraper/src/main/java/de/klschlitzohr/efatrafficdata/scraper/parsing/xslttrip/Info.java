package de.klschlitzohr.efatrafficdata.scraper.parsing.xslttrip;

import lombok.Getter;

/**
 * Created on 17.10.2021
 *
 * @author KlSchlitzohr
 */
@Getter
public class Info {

    private String infoLinkText;
    private String infoLinkURL;
    private InfoText infoText;
    private Object paramList;

}
