package org.blockchain_java.blockchain.models.transaction;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TransactionOutput {
    private String address;
    private BigDecimal amount;
    private Boolean spent = false;
}
