package de.klschlitzohr.efatrafficdata.analysis.commands;

import lombok.extern.log4j.Log4j2;

import java.util.Scanner;

@Log4j2
public class CommandScanner {

    public CommandScanner() {
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
            log.info("Der server wird bald gestoppt");
        } else {
            log.info("Unknown command");
        }
    }

}
