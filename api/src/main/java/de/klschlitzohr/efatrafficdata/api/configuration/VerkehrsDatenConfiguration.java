package de.klschlitzohr.efatrafficdata.api.configuration;

import lombok.Data;

/**
 * Created on 07.12.2021
 *
 * @author DerMistkaefer
 */
@Data
public class VerkehrsDatenConfiguration {

    private VerkehrsDatenDatabaseConfiguration database;
    private String efaAPIKey;
    private String telegramBotToken;
    private String telegramChatId;
    private String sentryDsn;
}
