package de.klschlitzohr.efatrafficdata.analysis.worker;

import de.klschlitzohr.efatrafficdata.analysis.data.manager.DelayManager;
import de.klschlitzohr.efatrafficdata.analysis.data.manager.LineManager;
import de.klschlitzohr.efatrafficdata.analysis.data.manager.StationsManager;
import de.klschlitzohr.efatrafficdata.api.configuration.VerkehrsDatenConfiguration;
import de.klschlitzohr.efatrafficdata.api.configuration.VerkehrsDatenConfigurationLoader;
import de.klschlitzohr.efatrafficdata.api.data.model.OwnDelay;
import de.klschlitzohr.efatrafficdata.api.database.DatabaseManager;
import de.klschlitzohr.efatrafficdata.api.database.repositories.LineRepository;
import de.klschlitzohr.efatrafficdata.api.database.repositories.SqlLineRepository;
import de.klschlitzohr.efatrafficdata.api.database.repositories.SqlStationsRepository;
import de.klschlitzohr.efatrafficdata.api.database.repositories.StationsRepository;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Log4j2
public class VerkehrsDatenResult {

    private final VerkehrsDatenConfiguration configuration;
    private final LineRepository lineRepository;
    private final StationsRepository stationsRepository;
    private final DatabaseManager databaseManager;
    private final StationsManager stationsManager;
    private final LineManager lineManager;
    private final DelayManager delayManager;

    public VerkehrsDatenResult() {
        configuration = VerkehrsDatenConfigurationLoader.loadConfiguration("analysis.config");
        databaseManager = new DatabaseManager(configuration.getDatabase());
        lineRepository = new SqlLineRepository(databaseManager);
        stationsRepository = new SqlStationsRepository(databaseManager);
        stationsManager = new StationsManager(stationsRepository);
        lineManager = new LineManager(lineRepository,stationsManager);
        delayManager = new DelayManager(lineRepository,lineManager);
    }

    public void start() {
        log.info("Starting VerkehrsDatenDashBaord");
        System.out.println(LocalDateTime.now());
        delayManager.init(LocalDateTime.now().minusHours(24*3), LocalDateTime.now());
        System.out.println(LocalDateTime.now());
        List<HistoryLine> historyLines = new ArrayList<>();
        System.out.println(delayManager.getDelays().size());
        int i = 0;
        for (OwnDelay line : delayManager.getDelays()) {
            i++;
            if (i % 10000 == 0)
                System.out.println(i + " " + LocalDateTime.now());
            if (historyLines.stream().anyMatch(historyLine -> historyLine.getOwnLine().getOwnLineID() == line.getLineID())) {
                HistoryLine historyLine = historyLines.stream().filter(historyLine1 -> historyLine1.getOwnLine().getOwnLineID() == line.getLineID()).findFirst().get();
                historyLine.getHistory().put(line.getLocalDateTime(), line.getDelay());
            }
            HistoryLine historyLine = new HistoryLine(lineManager.getOwnLinebyOwnId(line.getLineID()), line.getKey(), lineManager.getLocalTimeByOwnId(line.getLineID(),line.getKey()));
            historyLine.addHistory(line.getLocalDateTime(), line.getDelay());
            historyLines.add(historyLine);
        }
        System.out.println(LocalDateTime.now());
        System.out.println(historyLines.size());
    }

   /* public void getLiveLines(List<HistoryLine> historyLines) {
        databaseManager.getUpdate("TRUNCATE TABLE `tempDelays`");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        for (HistoryLine line : historyLines) {
            for (LocalDateTime history : line.getHistory().keySet()) {
                databaseManager.getUpdate("INSERT INTO `tempDelays` VALUES ('" + line.getOwnLine().getOwnLineID() + "_" + line.getKey() + "', '" + history.format(formatter) + "','" + line.getHistory().get(history) + "');");
                //System.out.println("added");
            }
        }
    }*/

}
