package de.klschlitzohr.efatrafficdata.api.database.repositories;

import de.klschlitzohr.efatrafficdata.api.data.model.OwnDelay;
import de.klschlitzohr.efatrafficdata.api.data.model.OwnLine;
import de.klschlitzohr.efatrafficdata.api.data.model.OwnLineStart;
import de.klschlitzohr.efatrafficdata.api.data.model.OwnLineStop;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Created on 07.12.2021
 *
 * @author DerMistkaefer
 */
public interface LineRepository {

    List<OwnLine> getAllLinesWithStops();
    List<OwnLineStart> getAllLineStarts();
    ArrayList<OwnLineStop> getLineStopsByLineId(int ownLineID);
    List<OwnDelay> getAllLineDelays();
    List<OwnDelay> getLineDelaysFromTo(LocalDateTime start, LocalDateTime end);
    int insertLine(OwnLine line);
    void insertLineStart(OwnLineStart lineStart);
    void insertLineStop(OwnLineStop lineStop);
    void insertLineDelay(OwnDelay delay);
    void updateLineDelay(OwnDelay delay, int newDelay);
    void insertPath(OwnLine ownLine, String path);
}
