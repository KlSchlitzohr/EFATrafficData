package de.klschlitzohr.efatrafficdata.api.database.repositories;

import de.klschlitzohr.efatrafficdata.api.data.model.OwnStation;
import de.klschlitzohr.efatrafficdata.api.database.DatabaseManager;
import lombok.extern.log4j.Log4j2;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created on 07.12.2021
 *
 * @author DerMistkaefer
 */
@Log4j2
public class SqlStationsRepository implements StationsRepository {

    private final DatabaseManager databaseManager;

    public SqlStationsRepository(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    @Override
    public ArrayList<OwnStation> getAllStations() {
        ArrayList<OwnStation> stations = new ArrayList<>();
        ResultSet resultSet = databaseManager.getResult("SELECT * FROM stations;");
        try {
            while (resultSet.next()) {
                OwnStation ownStation = new OwnStation(resultSet.getInt("ownStopID"),
                        resultSet.getInt("stopID"),
                        resultSet.getString("gid"),
                        resultSet.getFloat("x"),
                        resultSet.getFloat("y"),
                        resultSet.getString("place"),
                        resultSet.getString("name"),
                        resultSet.getString("nameWithPlace"));
                stations.add(ownStation);
            }
        } catch (Exception e) {
            log.error(e);
        }
        return stations;
    }

    @Override
    public int insertStation(OwnStation station) {
        int stopID = station.getStopID();
        int ownStopID = 0;
        databaseManager.getUpdate("INSERT INTO stations VALUES('0','" + stopID + "','" + station.getStopID() + "','" + station.getX() + "','" + station.getY() + "','" +
                station.getPlace().replace("'","\\'") + "','" + station.getName().replace("'","\\'") + "','" +
                station.getNameWithPlace().replace("'","\\'") + "');");
        ResultSet resultSet = databaseManager.getResult("SELECT ownStopID from stations WHERE stopID = "  + stopID);
        try {
            while (resultSet.next()) {
                ownStopID = resultSet.getInt("ownStopID");
            }
        } catch (Exception e) {
            log.error(e);
        }
        return ownStopID;
    }

    @Override
    public void updateXY(int ownStopID, double xFlat, double yFlat) {
        databaseManager.getUpdate("UPDATE stations SET x = '"+ xFlat + "' , y = '" + yFlat + "' WHERE ownStopID = '" + ownStopID + "';");
    }

    @Override
    public void updateNames(int ownStopID, String name, String nameWithPlace) {
        databaseManager.getUpdate("UPDATE stations SET name = \"" + name + "\" , nameWithPlace = \"" + nameWithPlace + "\" WHERE ownStopID = '" + ownStopID + "';");
    }

    @Override
    public Map<String, Integer> getAllStationsManuell() {
        Map<String,Integer> stationManuelList = new HashMap<>();
        ResultSet resultSetManuel = databaseManager.getResult("SELECT * FROM stationsMANUELL");
        try {
            while (resultSetManuel.next()) {
                stationManuelList.put(resultSetManuel.getString("nameWithPlace"), resultSetManuel.getInt("stopID"));
            }
        } catch (Exception e) {
            log.error(e);
        }
        return stationManuelList;
    }
}
