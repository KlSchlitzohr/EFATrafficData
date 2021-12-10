package de.klschlitzohr.efatrafficdata.scraper.utils;

import lombok.Data;

/**
 * Created on 24.09.2021
 *
 * @author KlSchlitzohr
 */
@Data
public class HttpResponse {

    private final String data;
    private final long duration;
    private final int responseCode;

}
