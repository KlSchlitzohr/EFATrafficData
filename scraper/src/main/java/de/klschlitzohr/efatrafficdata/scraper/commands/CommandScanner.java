package de.klschlitzohr.efatrafficdata.scraper.commands;

import de.klschlitzohr.efatrafficdata.scraper.VerkehrsDaten;
import lombok.extern.log4j.Log4j2;

import java.util.Scanner;

@Log4j2
public class CommandScanner {

    private final VerkehrsDaten verkehrsDaten;

    public CommandScanner(VerkehrsDaten verkehrsDaten) {
        this.verkehrsDaten = verkehrsDaten;
        waitForLine();
    }

    private void waitForLine() {
       new Thread(() -> {
           Scanner scanner = new Scanner(System.in);
           while (true) {
               executeCommand(scanner.next());
           }
       }).start();
    }

    private void executeCommand(String command) {
        if (command.equalsIgnoreCase("stop")) {
            log.info("Der Server wird bald stoppen!");
            verkehrsDaten.setStop(true);
        } else {
            log.info("Unknown command!");
        }
    }

}
