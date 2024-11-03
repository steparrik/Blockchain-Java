package org.blockchain_java.blockchain.service.transaction;

import org.blockchain_java.blockchain.models.transaction.TransactionInput;
import org.blockchain_java.blockchain.models.transaction.TransactionOutput;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class TransactionOutputService {

    public List<TransactionOutput> createOutputs(String fromAddress,
                                                 String toAddress,
                                                 BigDecimal amount,
                                                 Map<String, TransactionOutput> UTXO,
                                                 List<TransactionInput> transactionInputs){
        BigDecimal amountInInputs = transactionInputs.stream()
                .map(TransactionInput::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
        List<TransactionOutput> transactionOutputs = new ArrayList<>();

        for(TransactionInput transactionInput : transactionInputs){
            String key = transactionInput.getHash()+":"+transactionInput.getOutput();
            if(UTXO.containsKey(key)){
                UTXO.get(key).setSpent(true);
            }
        }

        TransactionOutput transactionOutputTo = new TransactionOutput(toAddress, amount, false);
        amountInInputs = amountInInputs.subtract(amount);
        TransactionOutput transactionOutputFrom = new TransactionOutput(fromAddress, amountInInputs, false);
        transactionOutputs.add(transactionOutputTo);
        transactionOutputs.add(transactionOutputFrom);

        return transactionOutputs;
    }


}
