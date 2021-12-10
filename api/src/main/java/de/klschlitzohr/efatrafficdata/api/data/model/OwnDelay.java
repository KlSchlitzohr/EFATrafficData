package de.klschlitzohr.efatrafficdata.api.data.model;

import de.klschlitzohr.efatrafficdata.api.database.repositories.LineRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Log4j2
public class OwnDelay {

    private int lineID;
    private int key;
    private LocalDateTime localDateTime;
    private int delay;

    public void writeToDatabase(LineRepository lineRepository) {
        new Thread(() -> {
            lineRepository.insertLineDelay(this);
            log.debug("Added Delay " + lineID + " " + key + " " + delay);
        }).start();
    }

    public void updateDelay(LineRepository lineRepository, int delay) {
        new Thread(() -> {
            lineRepository.updateLineDelay(this, delay);
            log.debug("Updated Delay " + lineID + " " + key + " " + delay);
        }).start();
    }

}
