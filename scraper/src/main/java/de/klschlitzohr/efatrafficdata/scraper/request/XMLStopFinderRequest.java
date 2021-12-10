package de.klschlitzohr.efatrafficdata.scraper.request;

import de.klschlitzohr.efatrafficdata.scraper.Main;
import de.klschlitzohr.efatrafficdata.scraper.parsing.xmlstopfinder.XMLSTOPFINDER;
import de.klschlitzohr.efatrafficdata.scraper.utils.HttpResponse;
import de.klschlitzohr.efatrafficdata.scraper.utils.HttpsRequestBuilder;
import de.klschlitzohr.efatrafficdata.scraper.utils.HttpsRequestType;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

/**
 * Created on 11.10.2021
 *
 * @author KlSchlitzohr
 */
@Getter
@Log4j2
public class XMLStopFinderRequest extends Request {

    private final String location;
    private XMLSTOPFINDER xmlstopfinder;

    public XMLStopFinderRequest(String location) {
        this.location = location.replace(",","%2C").replace(" ","+");
    }

    public XMLSTOPFINDER request() {
        getRequestHandler().checkTime();
        Main.getVerkehrsDaten().getDatabaseManager().getUpdate("INSERT INTO debug VALUES('0','0','0','0','" + location + "');");
        if (getRequestHandler().getXmlStopFinderRequests().stream().
                anyMatch(request -> request.getLocation().equalsIgnoreCase(location))) {
            log.debug("Chached Stopfinder");
            return getRequestHandler().getXmlStopFinderRequests().stream().
                    filter(request -> request.getLocation().equalsIgnoreCase(location)).findFirst().get().getXmlstopfinder();
        }

        HttpResponse httpResponse = new HttpsRequestBuilder("https://www.vrn.de/service/entwickler/trias-json/XML_STOPFINDER_REQUEST?outputFormat=JSON&language=de/old&stateless=1&coordOutputFormat=WGS84[DD.ddddd]&coordOutputFormatTail=7&locationServerActive=1&type_sf=any&name_sf=" + location + "&anyObjFilter_sf=0&reducedAnyPostcodeObjFilter_sf=64&reducedAnyTooManyObjFilter_sf=2&useHouseNumberList=true&anyMaxSizeHitList=100&maxChanges=1", HttpsRequestType.GET).getResponse();
        String response = httpResponse.getData();
        XMLSTOPFINDER xmlstopfinder = Main.getVerkehrsDaten().getGson().fromJson(response, XMLSTOPFINDER.class);
        this.xmlstopfinder = xmlstopfinder;
        getRequestHandler().addStopFinder(this);
        return xmlstopfinder;
    }
}
