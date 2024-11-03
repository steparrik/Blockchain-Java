package org.blockchain_java.blockchain.models.transaction;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Transaction {
    private String hash;
    private List<TransactionInput> inputs;
    private List<TransactionOutput> outputs;
    private long timestamp;
    private BigDecimal fee;
}
