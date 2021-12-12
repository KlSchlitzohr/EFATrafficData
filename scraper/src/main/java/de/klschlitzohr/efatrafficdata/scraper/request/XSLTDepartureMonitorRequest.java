package de.klschlitzohr.efatrafficdata.scraper.request;

import de.klschlitzohr.efatrafficdata.scraper.Main;
import de.klschlitzohr.efatrafficdata.scraper.parsing.xsltdm.XSLTDM;
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
public class XSLTDepartureMonitorRequest extends Request{

    private final String stopID;
    private XSLTDM xsltdm;

    public XSLTDepartureMonitorRequest(String stopID) {
        this.stopID = stopID;
    }

    public XSLTDM request() {
        log.debug("Requesting XSLTDepartureMonitor for stopID: " + stopID);
        getRequestHandler().checkTime();
        Main.getVerkehrsDaten().getDatabaseManager().getUpdate("INSERT INTO debug VALUES('0','" + stopID + "','0','0','null');");
        if (getRequestHandler().getXsltDepartureMonitorRequests().stream().
                anyMatch(request -> request.getStopID().equalsIgnoreCase(stopID))) {
            log.debug("Chached dmr");
            return getRequestHandler().getXsltDepartureMonitorRequests().stream().
                    filter(request -> request.getStopID().equalsIgnoreCase(stopID)).findFirst().get().getXsltdm();
        }

        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd HHmm");
        String dateString = format.format(new Date(System.currentTimeMillis()));
        String[] args = dateString.split(" ");
        HttpResponse httpResponse = new HttpsRequestBuilder("https://www.vrn.de/service/entwickler/trias-json/XSLT_DM_REQUEST?outputFormat=JSON&language=de&stateless=1&coordOutputFormat=WGS84[DD.ddddd]&coordOutputFormatTail=7&type_dm=stop&name_dm=" + stopID + "&itdDate=" + args[0] + "&itdTime=" + args[1] + "&useRealtime=1&mode=direct&ptOptionsActive=1&deleteAssignedStops_dm=1&useProxFootSearch=0&mergeDep=1&limit=100", HttpsRequestType.GET).getResponse();
        if (!httpResponse.getData().contains("points"))
            return null;
        XSLTDM xsltdm = Main.getVerkehrsDaten().getGson().fromJson(httpResponse.getData(), XSLTDM.class);
        this.xsltdm = xsltdm;
        getRequestHandler().addDmR(this);
        xsltdm.setXsltDepartureMonitorRequest(this);
        return xsltdm;
    }
}
