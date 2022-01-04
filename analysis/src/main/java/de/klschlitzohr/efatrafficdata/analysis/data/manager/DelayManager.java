package de.klschlitzohr.efatrafficdata.analysis.data.manager;

import de.klschlitzohr.efatrafficdata.api.data.model.OwnDelay;
import de.klschlitzohr.efatrafficdata.api.database.repositories.LineRepository;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Getter
@Log4j2
public class DelayManager {

    private final LineRepository lineRepository;
    private final LineManager lineManager;
    private List<OwnDelay> delays;

    public DelayManager(LineRepository lineRepository, LineManager lineManager) {
        this.lineRepository = lineRepository;
        this.lineManager = lineManager;
        this.delays = Collections.synchronizedList(new ArrayList<>());
    }

    public void init(LocalDateTime start, LocalDateTime end) {
        log.info("Loading Delays");
        delays = lineRepository.getLineDelaysFromTo(start, end);
    }

}
