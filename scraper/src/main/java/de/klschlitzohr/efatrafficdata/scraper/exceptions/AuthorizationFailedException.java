package de.klschlitzohr.efatrafficdata.scraper.exceptions;

public class AuthorizationFailedException extends Exception {

    public AuthorizationFailedException() {
        super("Your authorization is faulty. Please check your access token.");
    }

}
