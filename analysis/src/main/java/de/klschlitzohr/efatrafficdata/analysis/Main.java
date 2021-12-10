package de.klschlitzohr.efatrafficdata.analysis;

import lombok.Getter;

public class Main {

    @Getter
    public static VerkehrsDaten verkehrsDaten;

    public static void main(String[] args) {
        verkehrsDaten = new VerkehrsDaten();
        verkehrsDaten.test();
    }

}
