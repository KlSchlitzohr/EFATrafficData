package de.klschlitzohr.efatrafficdata.analysis;

import processing.core.PApplet;

import java.awt.*;

public class Processing extends PApplet {

    private int a = 200;
    private int b = 400;
    private int c = 600;
    private int d = 800;
    private int e = 1000;

    @Override
    public void settings() {
        size(1024, 1000);
    }

    @Override
    public void setup() {
        stroke(255);
        frameRate(5);
    }

    @Override
    public void draw() {

        // Draw background
        background(0);

        stroke(new Color(255, 0, 255).getRGB());
        a = moveLine(a);

        stroke(new Color(0, 255, 0).getRGB());
        b = moveLine(b);

        stroke(new Color(255, 255, 0).getRGB());
        c = moveLine(c);

        stroke(new Color(0, 255, 255).getRGB());
        d = moveLine(d);

        stroke(new Color(255, 0, 0).getRGB());
        e = moveLine(e);
    }

    private int moveLine(int number) {
        // Move line up (or wrap around the screen)
        number = number - 10;
        if (number < 0) {
            number = height;
        }

        // Draw line
        line(0, number, width, number-20);

        return number;
    }
}
