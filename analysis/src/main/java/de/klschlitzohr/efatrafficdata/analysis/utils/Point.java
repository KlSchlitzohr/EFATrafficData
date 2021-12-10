package de.klschlitzohr.efatrafficdata.analysis.utils;

import lombok.Data;

@Data
public class Point {

    private final double x;
    private final double y;

    public double distance(Point p) {
        double distance = 0;
        distance += Math.pow(p.getX() - this.getX(), 2);
        distance += Math.pow(p.getY() - this.getY(), 2);
        return Math.sqrt(distance);
    }

}
