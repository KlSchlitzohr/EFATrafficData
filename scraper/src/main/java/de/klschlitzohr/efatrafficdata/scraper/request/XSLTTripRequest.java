package de.klschlitzohr.efatrafficdata.scraper.request;

import de.klschlitzohr.efatrafficdata.scraper.Main;
import de.klschlitzohr.efatrafficdata.scraper.parsing.xslttrip.XSLTTRIP;
import de.klschlitzohr.efatrafficdata.scraper.utils.HttpResponse;
import de.klschlitzohr.efatrafficdata.scraper.utils.HttpsRequestBuilder;
import de.klschlitzohr.efatrafficdata.scraper.utils.HttpsRequestType;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created on 11.10.2021
 *
 * @author KlSchlitzohr
 */
@Getter
@Log4j2
public class XSLTTripRequest extends Request {

    private final String origin;
    private final String destination;
    private XSLTTRIP xslttrip;
    private Date date;

    public XSLTTripRequest(String origin, String destination) {
        this.origin = origin;
        this.destination = destination;
    }

    public XSLTTripRequest setTime(Date time) {
        date = time;
        return this;
    }

    public XSLTTRIP request() {
        getRequestHandler().checkTime();
        Main.getVerkehrsDaten().getDatabaseManager().getUpdate("INSERT INTO debug VALUES('0','0','" + origin + "','" + destination + "','null');");
        if (getRequestHandler().getXsltTripRequests().stream().
                anyMatch(request -> request.getOrigin().equalsIgnoreCase(origin) && request.getDestination().equalsIgnoreCase(destination))) {
            log.debug("Chached Trip");
            return getRequestHandler().getXsltTripRequests().stream().
                    filter(request -> request.getOrigin().equalsIgnoreCase(origin) && request.getDestination().equalsIgnoreCase(destination)).findFirst().get().getXslttrip();
        }

        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd HHmm");
        if (date == null)
            date = new Date(System.currentTimeMillis());

        String dateString = format.format(new Date(System.currentTimeMillis()));
        String[] args = dateString.split(" ");
        HttpResponse httpResponse = new HttpsRequestBuilder("https://www.vrn.de/service/entwickler/trias-json/XSLT_TRIP_REQUEST2?outputFormat=JSON&language=de/old&stateless=1&coordOutputFormat=WGS84[DD.ddddd]&coordOutputFormatTail=7&sessionID=0&requestID=0&coordListOutputFormat=string&type_origin=stop&name_origin=" + origin + "&type_destination=stop&name_destination=" + destination + "&itdDate=" + args[0] + "&itdTime=" + args[1] +"&itdTripDateTimeDepArr=dep&calcNumberOfTrips=6&ptOptionsActive=1&itOptionsActive=1&changeSpeed&useProxFootSearch=1&trITMOTvalue100=10&locationServerActive=1&useRealtime=1&nextDepsPerLeg=1", HttpsRequestType.GET).getResponse();
        String data = httpResponse.getData();
        XSLTTRIP xslttrip = Main.getVerkehrsDaten().getGson().fromJson(data, XSLTTRIP.class);
        this.xslttrip = xslttrip;
        getRequestHandler().addTrip(this);
        return xslttrip;
    }
}
