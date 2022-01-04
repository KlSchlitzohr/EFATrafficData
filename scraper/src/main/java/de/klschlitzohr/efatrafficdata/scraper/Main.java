package de.klschlitzohr.efatrafficdata.scraper;

import lombok.Getter;
import lombok.extern.log4j.Log4j2;

/**
 * Created on 24.10.2021
 *
 * @author KlSchlitzohr
 */
@Log4j2
public class Main {

    @Getter
    private static VerkehrsDaten verkehrsDaten;

    public static void main(String[] args) {
        verkehrsDaten = new VerkehrsDaten();
        verkehrsDaten.init();
        log.info("Done");

        //verkehrsDaten.loop();
        verkehrsDaten.test();
        log.info("STOP");
    }

}
