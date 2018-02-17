package org.neverfear.kav.core;

import java.util.HashMap;
import java.util.Map;

public class HashKeyValueStore implements KeyValueStore {
    private final Map<String, String> data = new HashMap<>();

    public String load(String key) {
        return data.get(key);
    }

    public void store(String key, String value) {
        data.put(key, value);
    }
}
