package de.klschlitzohr.efatrafficdata.api.database;

import com.mysql.cj.jdbc.MysqlDataSource;
import de.klschlitzohr.efatrafficdata.api.configuration.VerkehrsDatenDatabaseConfiguration;
import lombok.extern.log4j.Log4j2;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Created on 15.10.2021
 *
 * @author KlSchlitzohr
 */
@Log4j2
public class DatabaseManager {

    private final VerkehrsDatenDatabaseConfiguration databaseConfiguration;
    private Connection connection;

    public DatabaseManager(VerkehrsDatenDatabaseConfiguration databaseConfiguration) {
        this.databaseConfiguration = databaseConfiguration;
        connect();
    }

    public void connect() {
        if (!isConnected()) {
            MysqlDataSource mysqlDataSource = new MysqlDataSource();
            mysqlDataSource.setUser(databaseConfiguration.getUsername());
            mysqlDataSource.setPassword(databaseConfiguration.getPassword());
            mysqlDataSource.setServerName(databaseConfiguration.getHost());
            mysqlDataSource.setDatabaseName(databaseConfiguration.getDatabase());
            mysqlDataSource.setPort(databaseConfiguration.getPort());
            try {
                connection = mysqlDataSource.getConnection();
                log.info("[WRITE] Connected to Database!");
            } catch (SQLException ex) {
                log.error("[WRITE] Can't connect to database.", ex);
                System.exit(1);
            }
        }
    }

    public void disconnect() {
        if (isConnected()) {
            try {
                connection.close();
                log.info("[WRITE] Disconnected from Database!");
            } catch (SQLException e) {
                log.error("[WRITE] Can't disconnect from database.", e);
            }
            connection = null;
        }
    }

    private boolean isConnected() {
        try {
            return (connection != null) && !connection.isClosed();
        } catch (SQLException e) {
            log.error(e);
        }
        return false;
    }

    public ResultSet getResult(String query) {
        connect();
        try {
            Statement statement = connection.createStatement();
            return statement.executeQuery(query);
        } catch (SQLException e) {
            log.error("[WRITE] Result is faulty.", e);
        }
        return null;
    }

    public int getUpdate(String query) {
        connect();
        try {
            Statement statement = connection.createStatement();
            return statement.executeUpdate(query);
        } catch (SQLException e) {
            log.error("[WRITE] Result is faulty.", e);
        }
        return 0;
    }

}
