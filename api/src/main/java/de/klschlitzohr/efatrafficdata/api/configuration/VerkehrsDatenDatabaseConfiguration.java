package de.klschlitzohr.efatrafficdata.api.configuration;

import lombok.Data;

/**
 * Created on 07.12.2021
 *
 * @author DerMistkaefer
 */
@Data
public class VerkehrsDatenDatabaseConfiguration {

    private String host;
    private int port;
    private String database;
    private String username;
    private String password;
}
