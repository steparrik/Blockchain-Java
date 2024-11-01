package org.blockchain_java.blockchain.models;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class Block {
    private String hash;
    private String previousHash;
    private List<Transaction> transactions;
    private long nonce;
    private long index;
    private long timestamp;

    public Block() {
        this.timestamp = new Date().getTime();
    }
}
