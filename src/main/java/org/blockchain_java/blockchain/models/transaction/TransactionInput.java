package org.blockchain_java.blockchain.models.transaction;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TransactionInput {
    private String hash;
    private Long output;
    private String address;
    private BigDecimal amount;
}
