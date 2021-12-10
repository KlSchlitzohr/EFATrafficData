package de.klschlitzohr.efatrafficdata.analysis.utils;

import lombok.Getter;

import java.time.LocalTime;

@Getter
public class TempDelay {

    private final LocalTime localTime;
    private int delay;

    public TempDelay(LocalTime localTime, int delay) {
        this.delay = delay;
        this.localTime = localTime;
    }

    public void better(int dely) {
        delay += dely;
    }
}
