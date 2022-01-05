package de.klschlitzohr.efatrafficdata.analysis.worker;

import de.klschlitzohr.efatrafficdata.api.data.model.OwnLine;
import lombok.Getter;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;

@Getter
public class HistoryLine {

    private OwnLine ownLine;
    private int key;
    private LocalTime startTime;
    private Map<LocalDateTime,Integer> history;

    public HistoryLine(OwnLine ownLine, int key, LocalTime startTime) {
        this.ownLine = ownLine;
        this.key = key;
        this.history = new HashMap<>();
        this.startTime = startTime;
    }

    public void addHistory(LocalDateTime time, int value) {
        history.put(time, value);
    }

}
