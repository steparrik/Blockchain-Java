package org.blockchain_java.leveldb.config;

import org.fusesource.leveldbjni.JniDBFactory;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.Options;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;

@Configuration
public class LevelDBConfig {

    @Bean
    public DB levelDB() throws Exception {
        Options options = new Options();
        options.createIfMissing(true);
        File dbFile = new File("data/leveldb");
        if (!dbFile.exists()) {
            dbFile.mkdirs();
        }
        return JniDBFactory.factory.open(dbFile, options);
    }

}
