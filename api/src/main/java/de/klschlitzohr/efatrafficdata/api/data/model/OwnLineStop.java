package de.klschlitzohr.efatrafficdata.api.data.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created on 19.10.2021
 *
 * @author KlSchlitzohr
 */
@Getter
@AllArgsConstructor
public class OwnLineStop {

    private int lineID;
    private int stopID;
    private int departureDelay;
}
