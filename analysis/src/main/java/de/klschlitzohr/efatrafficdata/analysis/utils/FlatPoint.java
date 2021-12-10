package de.klschlitzohr.efatrafficdata.analysis.utils;

import lombok.Data;

@Data
public class FlatPoint {

    private final int x;
    private final int y;

    public int distance(FlatPoint p) {
        int distance = 0;
        distance += Math.pow(p.getX() - this.getX(), 2);
        distance += Math.pow(p.getY() - this.getY(), 2);
        return (int) Math.sqrt(distance);
    }

}
