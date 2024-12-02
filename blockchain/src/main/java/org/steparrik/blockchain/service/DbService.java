package org.steparrik.blockchain.service;

import org.iq80.leveldb.DBIterator;

public interface DbService {
    void put(String key, String value);
    String get(String key);
    void delete(String key);
    DBIterator iterator();
}
