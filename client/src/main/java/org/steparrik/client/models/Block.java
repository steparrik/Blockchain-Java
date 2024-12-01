package org.steparrik.client.models;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.steparrik.client.models.transaction.Transaction;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
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

    @Override
    public String toString() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        try {
            return mapper.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return super.toString();
        }
    }
}
