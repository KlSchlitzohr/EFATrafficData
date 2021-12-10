package de.klschlitzohr.efatrafficdata.api.database.repositories;

import de.klschlitzohr.efatrafficdata.api.data.model.OwnDelay;
import de.klschlitzohr.efatrafficdata.api.data.model.OwnLine;
import de.klschlitzohr.efatrafficdata.api.data.model.OwnLineStart;
import de.klschlitzohr.efatrafficdata.api.data.model.OwnLineStop;
import de.klschlitzohr.efatrafficdata.api.database.DatabaseManager;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created on 07.12.2021
 *
 * @author DerMistkaefer
 */
public class SqlLineRepository implements LineRepository {

    private final DatabaseManager databaseManager;

    public SqlLineRepository(DatabaseManager databaseManager) {
        this.databaseManager = databaseManager;
    }

    @Override
    public List<OwnLine> getAllLinesWithStops() {
        Map<Integer, OwnLine> lines = new HashMap<>();
        ResultSet resultSet = databaseManager.getResult("SELECT l.ownLineID, l.stateless, l.name, l.number, l.operator, ls.lineID ls_lineID, ls.stopID ls_stopID, ls.departureDelay ls_departureDelay FROM line l LEFT JOIN lineStops ls ON ls.lineID = l.ownLineID ORDER BY l.ownLineID, ls.departureDelay;");
        try {
            while (resultSet.next()) {
                int ownLineId = resultSet.getInt("ownLineID");
                if (!lines.containsKey(ownLineId)) {
                    // Add Line
                    OwnLine ownLine = new OwnLine(resultSet.getInt("ownLineID"),
                            resultSet.getString("stateless"),
                            resultSet.getString("name"),
                            resultSet.getString("number"),
                            resultSet.getString("operator"));
                    lines.put(ownLineId, ownLine);
                }
                OwnLine line = lines.get(ownLineId);
                line.getOwnLineStops().add(parseLineStop(resultSet, "ls_"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new ArrayList<>(lines.values());
    }

    @Override
    public List<OwnLineStart> getAllLineStarts() {
        ArrayList<OwnLineStart> linesStart = new ArrayList<>();
        ResultSet resultSet = databaseManager.getResult("SELECT * FROM lineStarts;");
        try {
            while (resultSet.next()) {
                linesStart.add(new OwnLineStart(resultSet.getInt("lineID"),
                        resultSet.getInt("linekey"),
                        resultSet.getTime("startTime").toLocalTime()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return linesStart;
    }

    @Override
    public ArrayList<OwnLineStop> getLineStopsByLineId(int ownLineID) {
        ArrayList<OwnLineStop> ownLineStops = new ArrayList<>();
        ResultSet resultSet = databaseManager.getResult("SELECT * from lineStops WHERE lineID = " + ownLineID);
        try {
            while (resultSet.next()) {
                ownLineStops.add(parseLineStop(resultSet, ""));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ownLineStops;
    }

    @Override
    public List<OwnDelay> getAllLineDelays() {
        ArrayList<OwnDelay> delays = new ArrayList<>();
        ResultSet resultSet = databaseManager.getResult("SELECT * FROM lineDelays;");
        try {
            while (resultSet.next()) {
                OwnDelay ownDelay = new OwnDelay(resultSet.getInt("lineID"),
                        resultSet.getInt("linekey"),
                        resultSet.getTimestamp("requestTime").toLocalDateTime(),
                        resultSet.getInt("delay"));
                delays.add(ownDelay);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return delays;
    }

    private OwnLineStop parseLineStop(ResultSet resultSet, String prefix) throws SQLException {
        return new OwnLineStop(resultSet.getInt(prefix + "lineID"), resultSet.getInt(prefix + "stopID"), resultSet.getInt(prefix + "departureDelay"));
    }

    @Override
    public int insertLine(OwnLine line) {
        int ownLineID = 0;
        databaseManager.getUpdate("INSERT INTO line VALUES('0','" + line.getStateless() + "','" + line.getName() + "','" + line.getNumber() + "','" + line.getOperator() + "');");
        ResultSet resultSet = databaseManager.getResult("SELECT ownLineID from line WHERE stateless = \"" + line.getStateless() + "\";");
        try {
            while (resultSet.next()) {
                ownLineID = resultSet.getInt("ownLineID");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ownLineID;
    }

    @Override
    public void insertLineStart(OwnLineStart lineStart) {
        databaseManager.getUpdate("INSERT INTO lineStarts VALUES('" + lineStart.getLineID() + "','" + lineStart.getKey() + "','" + Time.valueOf(lineStart.getStartTime()) + "');");
    }

    @Override
    public void insertLineStop(OwnLineStop lineStop) {
        databaseManager.getUpdate("INSERT INTO lineStops VALUES('" + lineStop.getLineID() + "','"
                + lineStop.getStopID()  + "','" + lineStop.getDepartureDelay() + "');");
    }

    @Override
    public void insertLineDelay(OwnDelay delay) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        databaseManager.getUpdate("INSERT INTO lineDelays VALUES('" + delay.getLineID() + "','" + delay.getKey() + "','" + delay.getLocalDateTime().format(formatter) + "','" + delay.getDelay() + "');");
    }

    @Override
    public void updateLineDelay(OwnDelay delay, int newDelay) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        databaseManager.getUpdate("UPDATE lineDelays SET delay = '" + newDelay + "' WHERE lineID = '" + delay.getLineID() + "' AND linekey = '" + delay.getKey() + "' AND requestTime = '" + delay.getLocalDateTime().format(formatter) + "';");
    }
}
