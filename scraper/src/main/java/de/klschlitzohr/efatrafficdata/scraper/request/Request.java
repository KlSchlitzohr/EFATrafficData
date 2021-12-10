package de.klschlitzohr.efatrafficdata.scraper.request;

import de.klschlitzohr.efatrafficdata.scraper.Main;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * Created on 24.10.2021
 *
 * @author KlSchlitzohr
 */
@Getter
public class Request {

    private final long time;
    private final LocalDateTime localDateTime;
    private final RequestHandler requestHandler;

    public Request() {
        this.requestHandler = Main.getVerkehrsDaten().getRequestHandler();
        this.time = System.currentTimeMillis();
        this.localDateTime = LocalDateTime.now();
    }
}
