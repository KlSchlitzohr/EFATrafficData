package de.klschlitzohr.efatrafficdata.scraper.utils;

import de.klschlitzohr.efatrafficdata.api.data.model.OwnLine;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class TempStation {

    private final int stationID;
    private int count;
    private final List<OwnLine> lineList;

    public TempStation(int stationID, int start) {
        this.stationID = stationID;
        this.lineList = new ArrayList<>();
        count = start;
    }

    public void better(int better) {
        count += better;
    }

    public void addLine(OwnLine line) {
        lineList.add(line);
    }
}
