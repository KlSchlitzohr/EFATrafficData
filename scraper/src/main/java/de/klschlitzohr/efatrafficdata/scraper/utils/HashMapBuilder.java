package de.klschlitzohr.efatrafficdata.scraper.utils;

import java.util.HashMap;

public class HashMapBuilder<K, V> {

    private final HashMap<K, V> hashMap;

    public HashMapBuilder() {
        hashMap = new HashMap<>();
    }

    public HashMapBuilder<K, V> put(K key, V value) {
        hashMap.put(key, value);
        return this;
    }

    public HashMap<K, V> build() {
        return hashMap;
    }

}
