package org.neverfear.kav.core;

public interface KeyValueStore {
    String load(String key);

    void store(String key, String value);
}
