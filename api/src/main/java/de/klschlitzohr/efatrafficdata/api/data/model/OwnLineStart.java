package de.klschlitzohr.efatrafficdata.api.data.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalTime;

/**
 * Created on 31.10.2021
 *
 * @author KlSchlitzohr
 */
@Getter
@AllArgsConstructor
public class OwnLineStart {

    private int lineID;
    private int key;
    private LocalTime startTime;
}
