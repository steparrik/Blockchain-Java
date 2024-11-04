package org.blockchain_java.leveldb.service;

import lombok.RequiredArgsConstructor;
import org.iq80.leveldb.DB;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LevelDBService {
    private final DB levelDB;

    public void put(String key, String value) {
        levelDB.put(key.getBytes(), value.getBytes());
    }

    public String get(String key) {
        byte[] value = levelDB.get(key.getBytes());
        return value != null ? new String(value) : null;
    }

    public void delete(String key) {
        levelDB.delete(key.getBytes());
    }
}
