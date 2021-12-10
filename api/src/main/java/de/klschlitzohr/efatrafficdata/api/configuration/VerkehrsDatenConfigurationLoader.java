package de.klschlitzohr.efatrafficdata.api.configuration;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 * Created on 07.12.2021
 *
 * @author DerMistkaefer
 */
public final class VerkehrsDatenConfigurationLoader {

    public static VerkehrsDatenConfiguration loadConfiguration(String configurationFile) {
        Properties prop = new Properties();
        try (FileInputStream fis = new FileInputStream(configurationFile)) {
            prop.load(fis);
        } catch (FileNotFoundException ex) {
            System.err.println("[CONFIGURATION] - The Configuration File '"+ configurationFile +"' could not be found.");
            ex.printStackTrace();
            System.exit(1);
        } catch (IOException ex) {
            System.err.println("[CONFIGURATION] - Error Loading Configuration File '"+ configurationFile + "'. " + ex.getLocalizedMessage());
            ex.printStackTrace();
            System.exit(1);
        }

        VerkehrsDatenDatabaseConfiguration databaseConfiguration = new VerkehrsDatenDatabaseConfiguration();
        databaseConfiguration.setHost(prop.getProperty("database.host"));
        databaseConfiguration.setPort(Integer.parseInt(prop.getProperty("database.port")));
        databaseConfiguration.setDatabase(prop.getProperty("database.database"));
        databaseConfiguration.setUsername(prop.getProperty("database.username"));
        databaseConfiguration.setPassword(prop.getProperty("database.password"));

        VerkehrsDatenConfiguration configuration = new VerkehrsDatenConfiguration();
        configuration.setDatabase(databaseConfiguration);
        configuration.setEfaAPIKey(prop.getProperty("efa.api-key"));
        configuration.setTelegramBotToken(prop.getProperty("telegram.bot-token"));
        configuration.setTelegramChatId(prop.getProperty("telegram.chat-id"));
        configuration.setSentryDsn(prop.getProperty("sentry.dsn"));

        return configuration;
    }
}
