package org.steparrik.client.config.db;

import org.fusesource.leveldbjni.JniDBFactory;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.Options;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;

@Configuration
public class ClientDbConfig {

    @Bean
    public DB clientDb() throws Exception {
        Options options = new Options();
        options.createIfMissing(true);
        File dbFile = new File("clientData");
        if (!dbFile.exists()) {
            dbFile.mkdirs();
        }
        return JniDBFactory.factory.open(dbFile, options);
    }
}
