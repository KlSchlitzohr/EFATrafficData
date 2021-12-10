package de.klschlitzohr.efatrafficdata.analysis.utils;

import de.klschlitzohr.efatrafficdata.api.data.model.OwnLine;
import lombok.Data;
import lombok.Setter;

@Data
public class TempLine {

    private final OwnLine ownLine;
    private final int delay;
    private final int count;
    @Setter
    private int color;

}
