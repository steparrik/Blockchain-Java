package org.blockchain_java.blockchain.models;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Transaction {
    private String hash;
    private String from;
    private String to;
    private BigDecimal amount;
    private long timestamp;
}
