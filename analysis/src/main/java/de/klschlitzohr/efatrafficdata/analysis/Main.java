package de.klschlitzohr.efatrafficdata.analysis;

import lombok.Getter;
import processing.core.PApplet;

public class Main {

    @Getter
    public static VerkehrsDaten verkehrsDaten;

    public static void main(String[] args) {
        //PApplet.main(Processing.class.getName());
        verkehrsDaten = new VerkehrsDaten();
        verkehrsDaten.test();
    }

}
