package org.steparrik.blockchain.models.transaction;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class TransactionInput {
    private String transactionHash;
    private Long output;
    private String address;
    private List<String> witness;
    private BigDecimal amount;
}
