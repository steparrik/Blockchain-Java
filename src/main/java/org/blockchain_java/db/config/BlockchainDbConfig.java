package org.blockchain_java.db.config;

import org.fusesource.leveldbjni.JniDBFactory;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.Options;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;

@Configuration
public class BlockchainDbConfig {

    @Bean(name = "blockchainDb")
    public DB blockchainDb() throws Exception {
        Options options = new Options();
        options.createIfMissing(true);
        File dbFile = new File("data/blockchain");
        if (!dbFile.exists()) {
            dbFile.mkdirs();
        }
        return JniDBFactory.factory.open(dbFile, options);
    }
}
