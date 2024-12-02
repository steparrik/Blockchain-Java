package org.steparrik.client.service.wallet;

import com.google.gson.Gson;
import lombok.SneakyThrows;
import org.bouncycastle.crypto.digests.RIPEMD160Digest;
import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.util.encoders.Hex;
import org.iq80.leveldb.DB;
import org.iq80.leveldb.DBIterator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.steparrik.client.client.BlockchainApi;
import org.steparrik.client.models.Wallet;

import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.LinkedList;
import java.util.List;

@Service
public class WalletService  {
    private final DB clientDb;
    private final Gson gson;
    private final BlockchainApi blockchainApi;

    @Autowired
    public WalletService(DB clientDb,  Gson gson, BlockchainApi blockchainApi){
        this.clientDb = clientDb;
        this.gson = gson;
        this.blockchainApi = blockchainApi;
    }

    public void put(String key, String value) {
        blockchainApi.addTestOutput(gson.fromJson(value, Wallet.class).getAddress());
        clientDb.put(key.getBytes(), value.getBytes());
    }

    public String get(String key) {
        byte[] value = clientDb.get(key.getBytes());
        return value != null ? new String(value) : null;
    }

    public void delete(String key) {
        clientDb.delete(key.getBytes());
    }
}