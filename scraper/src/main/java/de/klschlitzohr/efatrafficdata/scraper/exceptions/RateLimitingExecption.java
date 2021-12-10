package de.klschlitzohr.efatrafficdata.scraper.exceptions;

public class RateLimitingExecption extends Exception {

    public RateLimitingExecption() {
        super("Rate limiting: Please wait a full minute before resuming API usage.");
    }
}
