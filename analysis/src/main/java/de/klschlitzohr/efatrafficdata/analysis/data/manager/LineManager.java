package de.klschlitzohr.efatrafficdata.analysis.data.manager;

import de.klschlitzohr.efatrafficdata.api.data.model.OwnLine;
import de.klschlitzohr.efatrafficdata.api.data.model.OwnLineStart;
import de.klschlitzohr.efatrafficdata.api.database.repositories.LineRepository;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created on 19.10.2021
 *
 * @author KlSchlitzohr
 */
@Getter
@Log4j2
public class LineManager {

    private List<OwnLine> lines;
    private final List<OwnLineStart> linesStart;
    private final LineRepository lineRepository;
    private final StationsManager stationsManager;

    public LineManager(LineRepository lineRepository, StationsManager stationsManager) {
        this.lineRepository = lineRepository;
        this.stationsManager = stationsManager;
        this.linesStart = new ArrayList<>();
        this.lines = new ArrayList<>();
        new Thread(this::init).start();
    }

    private void init() {
        log.info("Loading Lines");
        lines = lineRepository.getAllLinesWithStops();

        /*resultSet = databaseManager.getResult("SELECT * FROM lineStarts;");
        try {
            while (resultSet.next()) {
                linesStart.add(new OwnLineStart(resultSet.getInt("lineID"),
                        resultSet.getInt("linekey"),
                        resultSet.getTime("startTime").toLocalTime()));
            }
        } catch (Exception e) {
            log.error(e);
        }*/
    }

    public OwnLine getLineByStatless(String statless) {
        Optional<OwnLine> any = lines.stream().filter(line -> line.getStateless().equalsIgnoreCase(statless)).findAny();
        return any.orElse(null);
    }


    private LocalDateTime getTime(String timeString) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd HH:mm");
            return LocalDateTime.parse(timeString, formatter);
        } catch (Exception e) {
            log.error(e);
            return null;
        }
    }

    public OwnLine getOwnLinebyOwnId(int ownId) {
        return lines.stream().filter(line -> line.getOwnLineID() == ownId).findAny().orElse(null);
    }

}
